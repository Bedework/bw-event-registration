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
import org.bedework.eventreg.db.SysInfo;

import edu.rpi.cmt.calendar.XcalUtil.TzGetter;
import edu.rpi.cmt.timezones.Timezones;
import edu.rpi.sss.util.Util;

import net.fortuna.ical4j.model.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author douglm
 *
 */
public class SessionManager  {
  protected final Log logger = LogFactory.getLog(getClass());
//  public final static String EVENTINFOURL = "http://events.rpi.edu/event/eventView.do";

  private EventregDb db;

  private String currentUser;
  private boolean superUser;

  private HttpServletRequest request;

  /** Request parameter - comment
   */
  public static final String reqparComment = "comment";

  /** Request parameter - href
   */
  public static final String reqparHref = "href";

  /** Request parameter - number of tickets
   */
  public static final String reqparNumtickets = "numtickets";

  /** Request parameter - ticket id
   */
  public static final String reqparTicketId = "ticketid";

  /** Request parameter - type
   */
  public static final String reqparType = "type";
  
  /** Type values
   */
  public static final String typeRegistered = "registered";
  public static final String typeWaiting = "waiting";
  public static final String typeHold = "hold";
  public static final String typeUnregistered = "unregistered";

  private SysInfo sys;
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
  }

  /**
   * @param db - object to handle db transactions.
   * @throws Throwable
   */
  public void setEventregDb(final EventregDb db) throws Throwable {
    try {
      this.db = db;

      db.open();
      setSysInfo(db.getSys());

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
  public void setSysInfo(final SysInfo val) {
    sys = val;
  }

  /**
   * @return system info set at entry.
   */
  public SysInfo getSysInfo() {
    return sys;
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
   * @return true if current user is a super user
   */
  public boolean getSuperUser() {
    return superUser;
  }

  /**
   * @param val
   */
  public void setCurrEvent(final Event val) {
    currEvent = val;
  }

  /**
   * @return event
   * @throws Throwable
   */
  public Event getCurrEvent() throws Throwable {
    if (currEvent != null) {
      // we already have an event; check to see if its href matches
      // the event being requested and return it if so.
      if (currEvent.getHref().equals(getHref())) {
        logger.debug("Returning cached event.");
        return currEvent;
      }
    }

    if (getHref() == null) {
      return null;
    }

    logger.debug("Fetching event.");
    currEvent = retrieveEvent(getHref());
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
   * @param reg
   * @throws Throwable
   */
  public void removeRegistration(final Registration reg) throws Throwable {
    db.delete(reg);
  }

  /**
   * @param ticketId
   * @param numTickets
   * @param comment
   * @param regType
   * @return true if found and updated
   * @throws Throwable
   */
  public boolean updateTicketById(final long ticketId,
                                  final int numTickets,
                                  final String comment,
                                  final String regType) throws Throwable {
    Registration reg = db.getByid(ticketId);

    if (reg == null) {
      return false;
    }

    Timestamp sqlDate = new Timestamp(new java.util.Date().getTime());

    reg.setLastmod(sqlDate.toString());
    reg.setNumTickets(numTickets);
    reg.setComment(comment);
    reg.setType(regType);

    db.update(reg);

    addChange(reg, Change.typeUpdReg,
              Change.lblUpdNumTickets,
              String.valueOf(numTickets));

    return true;
  }

  /**
   * @param reg
   * @param type
   * @throws Throwable
   */
  public void addChange(final Registration reg,
                        final String type) throws Throwable {
    Change c = new Change();

    c.setAuthid(getCurrentUser());
    c.setTicketid(reg.getTicketid());
    c.setLastmod(reg.getLastmod());
    c.setType(type);

    db.addChange(c);
  }

  /**
   * @param reg
   * @param type
   * @param label
   * @param val
   * @throws Throwable
   */
  public void addChange(final Registration reg,
                        final String type,
                        final String label,
                        final String val) throws Throwable {
    Change c = new Change();

    c.setAuthid(getCurrentUser());
    c.setTicketid(reg.getTicketid());
    c.setLastmod(reg.getLastmod());
    c.setType(type);
    c.setName(label);
    c.setValue(val);

    db.addChange(c);
  }

  /**
   * @param numTickets
   * @param comment
   * @param regType
   * @param superUser
   * @return ticketId for registration
   * @throws Throwable
   */
  public Long registerUserInEvent(final int numTickets,
                                  final String comment,
                                  final String regType,
                                  final boolean superUser) throws Throwable {
    String href = getHref();

    logger.debug("Event details: " + getCurrentUser() + " " +
        href + " " +
        regType);

    /* we  let superusers register over and over, but not regular users */

    Timestamp sqlDate = new Timestamp(new java.util.Date().getTime());

    Registration reg;
    if (!superUser) {
      reg = db.getUserRegistration(href, getCurrentUser());

      if (reg != null) {
        reg.setLastmod(sqlDate.toString());
        reg.setNumTickets(numTickets);
        reg.setComment(comment);
        reg.setType(regType);

        db.update(reg);

        addChange(reg, Change.typeUpdReg,
                  Change.lblUpdNumTickets,
                  String.valueOf(numTickets));

        return reg.getTicketid();
      }
    }

    /* Create new entry */

    reg = new Registration();

    reg.setAuthid(getCurrentUser());
    reg.setComment(comment);
    reg.setType(regType);
    reg.setHref(href);
    reg.setNumTickets(numTickets);
    reg.setTicketid(getNextTicketId());

    reg.setCreated(sqlDate.toString());
    reg.setLastmod(reg.getCreated());

    db.add(reg);

    addChange(reg, Change.typeNewReg);

    return reg.getTicketid();
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
   * @return list of registrations
   * @throws Throwable
   */
  public List<Registration> getRegistrationsByHref(final String href) throws Throwable {
    return db.getByEvent(href);
  }

  /**
   * @return registration or null
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
   */
  public boolean getIsRegistered() throws Throwable {
    Registration reg = getRegistration();
    if (reg != null && reg.getType().equals(typeRegistered)) {
      return true;
    }
    return false;
  }
  
  /**
   * @return true if current user is on waiting list for current event
   */
  public boolean getIsWaiting() throws Throwable {
    Registration reg = getRegistration();
    if (reg != null && reg.getType().equals(typeWaiting)) {
      return true;
    }
    return false;
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
   * @return count of tickets allocated
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
    return db.getUserTicketCount(getCurrEvent().getHref(),
                                 getCurrentUser());
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

  /*
  public void resendConfirmationEmail(final int userid) {
    String [] recipients = new String [1];
    recipients[0] = getUserInfo().getEmail();

    String body = "Thank you for registering to reserve tickets for EMPAC Opening events.";
    body = body.concat("\n\nJust click this link to confirm your email address," );
    body = body.concat("\nand you can start reserving tickets:" +
    		"\nhttp://reg.empac.rpi.edu/empacreg/confirm.do?activationCode=" +
        this.getUserInfo().getGUID());
    body = body.concat("\n\nSee you there!\n-EMPAC\n\nQuestions? Call us @ 518.276.3921" );
    body = body.concat("\n\nExperimental Media and Performing Arts Center" );
    body = body.concat("\nRensselaer Polytechnic Institute" );
    body = body.concat("\n110 8th street" );
    body = body.concat("\nTroy, NY 12180" );
    body = body.concat("\nhttp://empac.rpi.edu" );

    / *    body = body.concat("Tickets will be available at will call on the day of the performance.\n");
    body = body.concat("All tickets must be picked up 30 minutes before performance time;\n");
    body = body.concat("Unclaimed tickets will bereleased 30 minutes before performance time.\n\n ");


    body = body.concat("All venues are wheelchair accessible. Please contact the Ticket Office\n");
    body = body.concat("at empacboxoffice@rpi.edu or 518-276-3921 to reserve accessible seating\n");
    body = body.concat("or to request additional disability-related accommodation.\n\n");

    body = body.concat("Questions about your reservation? Contact the EMPAC Ticket Office\n");
    body = body.concat("at empacboxoffice@rpi.edu or 518-276-3921.\n\n");

    body = body.concat("Ticket Policies:\n");
    body = body.concat("No refunds/exchanges\n");
    body = body.concat("Use of cameras or recording devices in venues is not permitted.\n");
    body = body.concat("Late arrivals will be seated at the discretion of venue management.\n");
    body = body.concat("Not responsible for items lost, stolen or left behind.\n\n");

    body = body.concat("EMPAC\n");
    body = body.concat("Experimental Media & Performing Arts Center\n");
    body = body.concat("110 8th Street\n");
    body = body.concat("Troy, NY 12180\n");
    body = body.concat("518.276.4135\n");
    body = body.concat("http://empac.rpi.edu\n");* /

    try {
      Mailer.postMail(recipients,
                      "EMPAC Registration",
                      body,
                      "empacboxoffice@rpi.edu",
                      getUserInfo().getGUID());
    } catch (Exception e) {}

  }*/

  /* ====================================================================
   *                   Convenience methods
   * ==================================================================== */

  /** Get the root users list
   *
   * @return String[]   root users
   * @throws Exception
   */
  public String[] getRootUsersArray() throws Exception {
    if (getSysInfo() == null) {
      return new String[0];
    }

    return getSysInfo().getRootUsersArray();
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
  public void setRequest(final HttpServletRequest val) throws Exception {
    request = val;

    setCurrentUser(request.getRemoteUser());
    superUser = false;

    for (String s: getRootUsersArray()) {
      if (s.equals(getCurrentUser())) {
        superUser = true;
        break;
      }
    }
  }

  /**
   * @return incoming request
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /** Get a request parameter stripped of white space. Return null for zero
   * length.
   *
   * @param name    name of parameter
   * @return  String   value
   */
  public String getReqPar(final String name) {
    if (request == null) {
      return null;
    }

    return Util.checkNull(request.getParameter(name));
  }

  /**
   * @return number of tickets or -1 for no parameter
   */
  public int getTicketsRequested() {
    String reqpar = getReqPar(reqparNumtickets);

    if (reqpar == null) {
      return -1;
    }

    try {
      return Integer.parseInt(reqpar);
    } catch (Throwable t) {
      return -1; // XXX exception?
    }
  }

  /**
   * @return ticket id or null for no parameter
   */
  public Long getTicketId() {
    String reqpar = getReqPar(reqparTicketId);

    if (reqpar == null) {
      return (long)-1;
    }

    try {
      return Long.parseLong(reqpar);
    } catch (Throwable t) {
      return (long)-1; // XXX exception?
    }
  }

  /**
   * @return comment or null for no parameter
   */
  public String getComment() {
    return getReqPar(reqparComment);
  }

  /**
   * @return href or null for no parameter
   */
  public String getHref() {
    return getReqPar(reqparHref);
  }

  /**
   * @return type or null for no parameter
   */
  public String getType() {
    return getReqPar(reqparType);
  }

  private static transient volatile Long nextTicketId = (long)-1;

  private Long getNextTicketId() throws Throwable {
    synchronized (nextTicketId) {
      if (nextTicketId < 0) {
        Long nid = db.getMaxTicketId();
        if (nid == null) {
          // First time I guess
          nextTicketId = (long)1;
        } else {
          nextTicketId = nid + 1;
        }
      }

      Long tid = nextTicketId;
      nextTicketId++;

      return tid;
    }
  }
}
