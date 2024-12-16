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
import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.eventreg.requests.RegistrationAction;
import org.bedework.eventreg.service.EventregSvcMBean;
import org.bedework.util.calendar.XcalUtil.TzGetter;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.misc.Util;
import org.bedework.util.servlet.HttpServletUtils;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.timezones.TimezonesException;

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
public class SessionManager implements Logged {
  private final ChangeManager chgMan;

  private final Timestamp ts =
          new Timestamp(System.currentTimeMillis());

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

  private final static TzGetter tzs = id -> {
    try {
      return Timezones.getTz(id);
    } catch (final TimezonesException e) {
      throw new EventregException(e);
    }
  };

  private String currentFormName;

  private String currEmail;

  /**
   *
   */
  public SessionManager() {
    chgMan = new ChangeManager(this);
  }

  public EventregSvcMBean getSysInfo() {
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
   */
  public void setEventregDb(final EventregDb db) {
    try {
      this.db = db;

      db.setSysInfo(getSysInfo());

      Timezones.initTimezones(getSysInfo().getTzsUri());

      cnctr = new BwConnector(getSysInfo().getWsdlUri(), tzs);
    } catch (final Throwable t) {
      error(t);
      throw new EventregException(t);
    }
  }

  /**
   */
  public synchronized void openDb() {
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
   */
  public synchronized void rollbackDb() {
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
    db.close();
    return true;
  }

  /**
   *
   * @param request to add to queue
   * @return true if queued
   */
  public boolean queueRequest(final EventregRequest request) {
    return getSysInfo().queueRequest(request);
  }

  /**
   * @param val current authenticated user
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

  public void setCurrentFormName(final String val) {
    currentFormName = val;
  }

  public String getCurrentFormName() {
    return currentFormName;
  }

  /**
   * @return true if current user is an administrator
   */
  public boolean getAdminUser() {
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
   */
  public Event getCurrEvent() {
    return getCurrEvent(req.getHref());
  }

  /** Get the current email.
   *
   * @return email addr
   */
  public String getCurrEmail() {
    if (currEmail == null) {
      if (debug()) {
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
   */
  public Event getCurrEvent(final String href) {
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
   * @param c Change
   */
  public void addChange(final Change c) {
    db.addChange(c);
  }

  /**
   * @param lastmod to start at
   * @return changes
   */
  public List<Change> getChanges(final String lastmod) {
    return db.getChanges(lastmod);
  }

  public Registration getNewRegistration() {
    return db.getNewRegistration();
  }

  /**
   * @param r Registration
   */
  public void addRegistration(final Registration r) {
    db.add(r);
    queueRequest(new RegistrationAction(r));
  }

  /**
   * @param reg Registration
   */
  public void removeRegistration(final Registration reg) {
    db.delete(reg);
  }

  /**
   * @param reg Registration
   */
  public void updateRegistration(final Registration reg) {
    reg.setLastmod();

    db.update(reg);
    queueRequest(new RegistrationAction(reg));
  }

  /**
   * @param user owner of registrations
   * @return list of registrations
   */
  public List<Registration> getRegistrationsByUser(final String user) {
    return db.getByUser(user);
  }

  /**
   * @param id regid
   * @return referenced registration
   */
  public Registration getRegistrationById(final long id) {
    return db.getByid(id);
  }

  /**
   * @return all the current registrations
   */
  public List<Registration> getAllRegistrations() {
    return db.getAllRegistrations();
  }

  /**
   * @param href of event
   * @return all the current waiting registrations for the event
\   */
  public List<Registration> getWaiting(final String href) {
    return db.getWaiting(href);
  }

  /**
   * @param href of event
   * @return list of registrations
   */
  public List<Registration> getRegistrationsByHref(final String href) {
    return db.getByEvent(href);
  }

  /**
   * @return registration or null
   */
  public Registration getRegistration() {
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
   */
  public boolean getIsRegistered() {
    final var reg = getRegistration();
    return reg != null;
  }

  /**
   * @return true if current user is on waiting list for current event
   */
  public boolean getIsWaiting() {
    final var reg = getRegistration();

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
   * @param val true if current registration is full
   */
  public void setRegistrationFull(final boolean val) {
    registrationFull = val;
  }

  /**
   * @return count of registration entries
   */
  public long getRegistrantCount() {
    final boolean wasOpen = open;

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
   */
  public long getRegTicketCount() {
    final boolean wasOpen = open;

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
   */
  public long getWaitingTicketCount() {
    final boolean wasOpen = open;

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
   */
  public long getTicketCount() {
    final boolean wasOpen = open;

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
   */
  public long getUserTicketCount() {
    final boolean wasOpen = open;

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
   * @param val true if passed deadline
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
   * @param message current message string
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /* ========================================================
   *                   Current event methods
   * ======================================================== */

  /**
   * @param href of event
   * @return event
   */
  public Event retrieveEvent(final String href) {
    return cnctr.getEvent(href);
  }

  /**
   * @param reg Registration
   * @return event referenced by registration
   */
  public Event retrieveEvent(final Registration reg) {
    return cnctr.getEvent(reg.getHref());
  }

  /* ========================================================
   *                   Request methods
   * ======================================================== */

  public void logout() {
    if (debug()) {
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
   * @param val request
   */
  public boolean setReq(final Request val) {
    if (debug()) {
      debug("setReq for " + ts);
    }
    req = val;

    final String reqUser = HttpServletUtils.remoteUser(req.getRequest());

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

  /**
   * @return next id
   */
  public Long getNextRegistrationId() {
    return db.getNextRegistrationId();
  }

  /* ========================================================
   *                   Formdef methods
   * ======================================================== */

  public List<FormDef> getFormDefs() {
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

  public FormDef getFormDef(final String formName) {
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

  public void addFormDef(final FormDef val) {
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

  public void removeFormDef(final FormDef val) {
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

  public void updateFormDef(final FormDef val) {
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

  private String getEmail() {
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
        if (debug()) {
          debug("No account info for " + currentUser);
        }

        return currentUser + "@columbia.edu";
      }

      if (debug()) {
        debug("Retrieved account info " + ainfo);
      }

      return ainfo.getEmail();
      */
    final String email = getSelfregEmail();

    if (email != null) {
      return email;
    }

    final String defDomain = getSysInfo().getDefaultEmailDomain();

    if (defDomain == null) {
      return null;
    }

    return currentUser + "@" + defDomain;
  }


  private String getSelfregEmail() {
    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    try {
      final Context ctx = new InitialContext();
      final DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/selfregDS");

      con = ds.getConnection();
      con.setAutoCommit(false);
      //noinspection SqlResolve
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

  /* =========================================================
   *                   Logged methods
   * ========================================================= */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
