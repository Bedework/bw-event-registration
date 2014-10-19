/* ********************************************************************
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 */

package org.bedework.eventreg.bus;

import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.Registration;
import org.bedework.eventreg.service.EventregSvcMBean;
import org.bedework.util.calendar.XcalUtil.TzGetter;
import org.bedework.util.timezones.Timezones;

import net.fortuna.ical4j.model.TimeZone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author douglm
 *
 */
public class SessionManager {
  protected final Log logger = LogFactory.getLog(getClass());

  private ChangeManager chgMan;

  private EventregDb db;

  private String currentUser;

  private Request req;

  private Event currEvent;
  private boolean deadlinePassed;
  private boolean registrationFull;

  private String message = "";

  private boolean open;

  private BwConnector cnctr;

  private static TzGetter tzs = new TzGetter() {
    @Override
    public TimeZone getTz(final String id) throws Throwable {
      return Timezones.getTz(id);
    }
  };

  /**
   *
   */
  public SessionManager() {
    chgMan = new ChangeManager(this);
  }

  public EventregSvcMBean getSysInfo() throws Throwable {
    return ContextListener.getSysInfo();
  }

  /**
   * @return the change log manager
   */
  public ChangeManager getChangeManager() {
    return chgMan;
  }

  /**
   * @param db - object to handle db transactions.
   * @throws Throwable
   */
  public void setEventregDb(final EventregDb db) throws Throwable {
    try {
      this.db = db;

      db.setSysInfo(getSysInfo());

      db.open();

      Timezones.initTimezones(getSysInfo().getTzsUri());

      cnctr = new BwConnector(getSysInfo().getWsdlUri(), tzs);
    } catch (Throwable t) {
      logger.error(this, t);
      throw new Exception(t);
    } finally {
      if (db != null) {
        db.close();
      }
    }
  }

  /**
   * @throws Throwable
   */
  public synchronized void openDb() throws Throwable {
    if (db == null) {
      return;
    }

    if (open) {
      //???
      return;
    }
    db.open();
    open = true;
  }

  /**
   * @throws Throwable
   */
  public synchronized void rollbackDb() throws Throwable {
    if (db == null) {
      return;
    }

    db.rollback();
    open = false;
  }

  /**
   * @return false if something failed on close - probably a commit error
   */
  public synchronized boolean closeDb() {
    if (db == null) {
      return true;
    }

    open = false;
    return db.close();
  }

  /**
   * @param val
   */
  public void setCurrentUser(final String val) {
    currentUser = val;
  }

  /**
   * @return current authenticated user
   */
  public String getCurrentUser() {
    return currentUser;
  }

  /**
   * @return true if current user is an administrator
   * @throws Throwable
   */
  public boolean getAdminUser() throws Throwable {
    String adminToken = getSysInfo().getEventregAdminToken();

    if (adminToken == null) {
      return false;
    }

    return adminToken.equals(req.getAdminToken());
  }

  /**
   */
  public void flushCurrEvent() {
    currEvent = null;
    cnctr.flush();
  }

  /**
   * @return event
   * @throws Throwable
   */
  public Event getCurrEvent() throws Throwable {
    String href = req.getHref();

    if (currEvent != null) {
      // we already have an event; check to see if its href matches
      // the event being requested and return it if so.
      if (currEvent.getHref().equals(href)) {
        logger.debug("Returning cached event.");
        return currEvent;
      }
    }

    if (href == null) {
      return null;
    }

    logger.debug("Fetching event.");
    currEvent = retrieveEvent(href);
    return currEvent;
  }

  /**
   * @param c
   * @throws Throwable
   */
  public void addChange(final Change c) throws Throwable {
    db.addChange(c);
  }

  /**
   * @param ts
   * @return changes
   * @throws Throwable
   */
  public List<Change> getChanges(final String ts) throws Throwable {
    return db.getChanges(ts);
  }

  /**
   * @param r
   * @throws Throwable
   */
  public void addRegistration(final Registration r) throws Throwable {
    db.add(r);
  }

  /**
   * @param reg
   * @throws Throwable
   */
  public void removeRegistration(final Registration reg) throws Throwable {
    db.delete(reg);
  }

  /**
   * @param reg
   * @throws Throwable
   */
  public void updateRegistration(final Registration reg) throws Throwable {
    reg.setLastmod();

    db.update(reg);
  }

  /**
   * @param user
   * @return list of registrations
   * @throws Throwable
   */
  public List<Registration> getRegistrationsByUser(final String user) throws Throwable {
    return db.getByUser(user);
  }

  /**
   * @param id
   * @return referenced registration
   * @throws Throwable
   */
  public Registration getRegistrationById(final long id) throws Throwable {
    return db.getByid(id);
  }

  /**
   * @return all the current registrations
   * @throws Throwable
   */
  public List<Registration> getAllRegistrations() throws Throwable {
    return db.getAllRegistrations();
  }

  /**
   * @param href
   * @return all the current waiting registrations for the event
   * @throws Throwable
   */
  public List<Registration> getWaiting(final String href) throws Throwable {
    return db.getWaiting(href);
  }

  /**
   * @param href
   * @return list of registrations
   * @throws Throwable
   */
  public List<Registration> getRegistrationsByHref(final String href) throws Throwable {
    return db.getByEvent(href);
  }

  /**
   * @return registration or null
   * @throws Throwable
   */
  public Registration getRegistration() throws Throwable {
    boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      logger.debug("Getting registration for " + getCurrentUser() + " on " + getCurrEvent().getHref());
      return db.getUserRegistration(getCurrEvent().getHref(), getCurrentUser());
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  /**
   * @return true if current user is registered for current event
   * @throws Throwable
   */
  public boolean getIsRegistered() throws Throwable {
    Registration reg = getRegistration();
    return reg != null;
  }

  /**
   * @return true if current user is on waiting list for current event
   * @throws Throwable
   */
  public boolean getIsWaiting() throws Throwable {
    Registration reg = getRegistration();

    if (reg == null) {
      return false;
    }

    return reg.getNumTickets() < reg.getTicketsRequested();
  }

  /**
   * @return true if current registration is full
   */
  public boolean getRegistrationFull() {
    return registrationFull;
  }

  /**
   * @param val
   */
  public void setRegistrationFull(final boolean val) {
    registrationFull = val;
  }

  /**
   * @return count of registration entries
   * @throws Throwable
   */
  public long getRegistrantCount() throws Throwable {
    boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      return db.getRegistrantCount(getCurrEvent().getHref());
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  /**
   * @return count of tickets allocated as "registered" or "hold"
   * @throws Throwable
   */
  public long getRegTicketCount() throws Throwable {
    boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      logger.debug("Getting registered/held ticket count for " + getCurrEvent().getHref());
      return db.getRegTicketCount(getCurrEvent().getHref());
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  /**
   * @return count of tickets allocated as "waiting"
   * @throws Throwable
   */
  public long getWaitingTicketCount() throws Throwable {
    boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      logger.debug("Getting waiting list ticket count for " + getCurrEvent().getHref());
      return db.getWaitingTicketCount(getCurrEvent().getHref());
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  /**
   * @return count of all tickets (registered, waiting, etc)
   * @throws Throwable
   */
  public long getTicketCount() throws Throwable {
    boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      logger.debug("Getting ticket count for " + getCurrEvent().getHref());
      return db.getTicketCount(getCurrEvent().getHref());
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  /**
   * @return count of tickets allocated to user for event
   * @throws Throwable
   */
  public long getUserTicketCount() throws Throwable {
    boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      return db.getUserTicketCount(getCurrEvent().getHref(),
                                 getCurrentUser());
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  /**
   * @return true if deadline passed
   */
  public boolean getDeadlinePassed() {
    return deadlinePassed;
  }

  /**
   * @param val
   */
  public void setDeadlinePassed(final boolean val) {
    deadlinePassed = val;
  }

  /**
   * @return current message string
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /* ====================================================================
   *                   Current event methods
   * ==================================================================== */

  /**
   * @param href
   * @return event
   * @throws Throwable
   */
  public Event retrieveEvent(final String href) throws Throwable {
    return cnctr.getEvent(href);
  }

  /**
   * @param reg
   * @return event referenced by registration
   * @throws Throwable
   */
  public Event retrieveEvent(final Registration reg) throws Throwable {
    return cnctr.getEvent(reg.getHref());
  }

  /* ====================================================================
   *                   Request methods
   * ==================================================================== */

  /**
   * @param val
   * @throws Exception
   */
  public void setReq(final Request val) throws Exception {
    req = val;

    setCurrentUser(req.getRequest().getRemoteUser());
  }

  /**
   * @return incoming request util object
   */
  public Request getReq() {
    return req;
  }

  private static transient volatile Long nextRegId = (long)-1;

  /**
   * @return next id
   * @throws Throwable
   */
  public Long getNextRegistrationId() throws Throwable {
    synchronized (nextRegId) {
      if (nextRegId < 0) {
        Long nid = db.getMaxRegistrationId();
        if (nid == null) {
          // First time I guess
          nextRegId = (long)1;
        } else {
          nextRegId = nid + 1;
        }
      }

      Long rid = nextRegId;
      nextRegId++;

      return rid;
    }
  }
}
