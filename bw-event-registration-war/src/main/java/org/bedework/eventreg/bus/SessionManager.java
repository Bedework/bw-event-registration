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

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.Registration;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.eventreg.requests.RegistrationAction;
import org.bedework.eventreg.service.EventregSvcMBean;
import org.bedework.util.calendar.XcalUtil.TzGetter;
import org.bedework.util.misc.Logged;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.Timezones;

import net.fortuna.ical4j.model.TimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * @author douglm
 *
 */
public class SessionManager extends Logged {
  private ChangeManager chgMan;

  private Timestamp ts = new Timestamp(System.currentTimeMillis());

  private EventregDb db;

  private String currentUser;

  private String currentCalsuite;

  private Request req;

  private Event currEvent;
  private boolean deadlinePassed;
  private boolean registrationFull;

  private String message = "";

  private boolean open;

  private BwConnector cnctr;

  private final static TzGetter tzs = new TzGetter() {
    @Override
    public TimeZone getTz(final String id) throws Throwable {
      return Timezones.getTz(id);
    }
  };

  private String currentFormName;

  private String currEmail;

  /**
   *
   */
  public SessionManager() throws EventregException {
    chgMan = new ChangeManager(this);
  }

  public EventregSvcMBean getSysInfo() throws EventregException {
    try {
      return ContextListener.getSysInfo();
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
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
    } catch (final Throwable t) {
      error(t);
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
   *
   * @param request to add to queue
   * @return true if queued
   * @throws Throwable
   */
  public boolean queueRequest(final EventregRequest request) throws Throwable {
    return getSysInfo().queueRequest(request);
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
   */
  public void resetCurrentCalsuite() {
    currentCalsuite = null;
  }

  /**
   * @param val href
   */
  public void setCurrentCalsuite(final String val) {
    if (val != null) {
      currentCalsuite = val;
    }
  }

  /**
   * @return current authenticated user
   */
  public String getCurrentCalsuite() {
    return currentCalsuite;
  }

  public void setCurrentFormName(final String val) throws Exception {
    currentFormName = val;
  }

  public String getCurrentFormName() {
    return currentFormName;
  }

  /**
   * @return true if current user is an administrator
   * @throws Throwable
   */
  public boolean getAdminUser() throws Throwable {
    final String adminToken = getSysInfo().getEventregAdminToken();

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

  /** Get the current event. If we haven't yet retrieved it we retrieve
   * the event specified by the request href parameter.
   *
   * @return event
   * @throws Throwable
   */
  public Event getCurrEvent() throws Throwable {
    return getCurrEvent(req.getHref());
  }

  /** Get the current email.
   *
   * @return email addr
   * @throws Throwable
   */
  public String getCurrEmail() throws Throwable {
    if (currEmail == null) {
      if (debug) {
        debug("Try to get email for " + currentUser);
      }

      currEmail = getEmail();
    }

    return currEmail;
  }

  /** Get the current event. If we haven't yet retrieved it we retrieve
   * the event specified by the href parameter.
   *
   * @return event
   * @throws Throwable
   */
  public Event getCurrEvent(final String href) throws Throwable {
    // we already have an event; check to see if its href matches
    // the event being requested and return it if so.
    if ((currEvent != null) &&
            currEvent.getHref().equals(href)) {
      debug("Returning cached event.");
      return currEvent;
    }

    if (href == null) {
      return null;
    }

    debug("Fetching event.");
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
    queueRequest(new RegistrationAction(r));
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
    queueRequest(new RegistrationAction(reg));
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
    final boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }
      debug("Getting registration for " + getCurrentUser() + " on " + getCurrEvent()
              .getHref());
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
    final Registration reg = getRegistration();
    return reg != null;
  }

  /**
   * @return true if current user is on waiting list for current event
   * @throws Throwable
   */
  public boolean getIsWaiting() throws Throwable {
    final Registration reg = getRegistration();

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
      debug("Getting registered/held ticket count for " + getCurrEvent()
              .getHref());
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
      debug("Getting waiting list ticket count for " + getCurrEvent()
              .getHref());
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
      debug("Getting ticket count for " + getCurrEvent().getHref());
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
   * @param href of event
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

  public void logout() {
    if (debug) {
      debug("logout for " + ts);
    }

    try {
      final HttpSession sess = req.getRequest().getSession(false);

      if (sess != null) {
        sess.invalidate();
      }
    } catch (final Throwable t) {
      warn("Exception on logout " + t.getLocalizedMessage());
    }
  }

  /**
   * @param val
   * @throws Exception
   */
  public boolean setReq(final Request val) throws Throwable {
    if (debug) {
      debug("setReq for " + ts);
    }
    req = val;

    final String reqUser = req.getRequest().getRemoteUser();

    if (getCurrentUser() != null) {
      final boolean forceLogout =
              !Util.equalsString(reqUser, getCurrentUser());

      if (forceLogout) {
        final HttpSession sess = req.getRequest().getSession(false);

        if (sess != null) {
          sess.invalidate();
        }

        req.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
      }
    }

    setCurrentUser(reqUser);
    setCurrentCalsuite(req.getCalsuite());

    if (req.formNamePresent()) {
      setCurrentFormName(req.getFormName());
    }

    return true;
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

  /* ====================================================================
   *                   Formdef methods
   * ==================================================================== */

  public List<FormDef> getFormDefs() throws Throwable {
    final boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }

      return db.getCalSuiteForms(currentCalsuite);
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  public FormDef getFormDef(final String formName) throws Throwable {
    final boolean wasOpen = open;

    try {
      if (!open) {
        openDb();
      }

      return db.getCalSuiteForm(formName,
                                currentCalsuite);
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  public void addFormDef(final FormDef val) throws Throwable {
    final boolean wasOpen = open;

    if (!val.getOwner().equals(currentCalsuite)) {
      throw new EventregException("Owner not current calsuite");
    }

    try {
      if (!open) {
        openDb();
      }

      db.add(val);
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  public void removeFormDef(final FormDef val) throws Throwable {
    final boolean wasOpen = open;

    if (!val.getOwner().equals(currentCalsuite)) {
      throw new EventregException("Owner not current calsuite");
    }

    try {
      if (!open) {
        openDb();
      }

      db.delete(val);
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  public void updateFormDef(final FormDef val) throws Throwable {
    final boolean wasOpen = open;

    if (!val.getOwner().equals(currentCalsuite)) {
      throw new EventregException("Owner not current calsuite");
    }

    try {
      if (!open) {
        openDb();
      }

      db.update(val);
    } finally {
      if (!wasOpen) {
        closeDb();
      }
    }
  }

  private String getEmail() throws EventregException {
    if (currentUser == null) {
      return null;
    }
      /*
      final Object[] pars = { currentUser };
      final String[] signature = { String.class.getName() };
      final AccountInfo ainfo =
              (AccountInfo)MBeanUtil.invoke(
                          "org.bedework.selfreg:service=config",
                          "getAccount", pars, signature);

      if (ainfo == null) {
        if (debug) {
          debug("No account info for " + currentUser);
        }

        return currentUser + "@columbia.edu";
      }

      if (debug) {
        debug("Retrieved account info " + ainfo);
      }

      return ainfo.getEmail();
      */
    String email = getSelfregEmail();

    if (email != null) {
      return email;
    }

    final String defDomain = getSysInfo().getDefaultEmailDomain();

    if (defDomain == null) {
      return null;
    }

    return currentUser + "@" + defDomain;
  }


  private String getSelfregEmail() throws EventregException {
    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    try {
      final Context ctx = new InitialContext();
      final DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/selfregDS");

      con = ds.getConnection();
      con.setAutoCommit(false);
      pstmt = con.prepareStatement(
              "select bw_email from bw_accounts " +
                      "WHERE bw_account = ?");
      pstmt.setString(1, currentUser);
      rs = pstmt.executeQuery();

      if (!rs.next()) {
        return null;
      }

      return rs.getString("bw_email");
    } catch (final Throwable t) {
      error(t);
      return null;
    } finally {

      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (final Throwable x) {
          warn("Exception on close " + x.getLocalizedMessage());
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (final Throwable ignored) {
        }
      }
      if (con != null) {
        try {
          con.close();
        } catch (final Throwable x) {
          warn("Exception on close " + x.getLocalizedMessage());
        }
      }
    }
  }
}
