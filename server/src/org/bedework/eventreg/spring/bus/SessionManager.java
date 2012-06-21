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

package org.bedework.eventreg.spring.bus;

import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.Registration;
import org.bedework.eventreg.db.SysInfo;

import edu.rpi.sss.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

  /** Request parameter - number of tickets
   */
  public static final String reqparNumtickets = "numtickets";

  /** Request parameter - ticket id
   */
  public static final String reqparTicketId = "ticketid";

  /** Request parameter - type
   */
  public static final String reqparType = "type";

  private SysInfo sys;
  private Event currEvent;
  private boolean deadlinePassed;
  private boolean registrationFull;

  private String message = "";

  /**
   *
   */
  public SessionManager() {
  }

  /**
   * @param db - object to handle db transactions.
   * @throws Exception
   */
  public void setEventregDb(final EventregDb db) throws Exception {
    try {
      this.db = db;

      setSysInfo(db.getSys());
    } catch (Throwable t) {
      logger.error(this, t);
      throw new Exception(t);
    }
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

  /* *
   * @param id
   * @return user info
   * @throws Exception
   * /
  public SysInfo findUser(final String id) throws Exception {
    return db.getUser(id);
  }

  /* *
   * @param email
   * @return user info
   * @throws Exception
   * /
  public SysInfo findUserByEmail(final String email) throws Exception {
    return db.getUserByEmail(email);
  }

  /* *
   * @param newUser
   * @throws Throwable
   * /
  public void addNewUser(final SysInfo newUser) throws Throwable {
    db.addUser(newUser);
  }

  */

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
   * @param currEvent
   */
  public void setCurrEvent(final Event currEvent) {
    this.currEvent = currEvent;
  }

  /**
   * @return event
   */
  public Event getCurrEvent() {
    return  this.currEvent;
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
  public boolean updateTicketById(final String ticketId,
                                  final int numTickets,
                                  final String comment,
                                  final String regType) throws Throwable {
    Registration reg = db.getByid(ticketId);

    if (reg == null) {
      return false;
    }

    reg.setNumTickets(numTickets);
    reg.setComment(comment);
    reg.setType(regType);

    db.update(reg);

    return true;
  }

  public String getURL(final String url) throws Throwable {
    return URLReader.read(url);
  }

  /**
   * @param numTickets
   * @param eventHref
   * @param comment
   * @param regType
   * @param superUser
   * @return ticketId for registration
   * @throws Throwable
   */
  public String registerUserInEvent(final int numTickets,
                                    final String eventHref,
                                    final String comment,
                                    final String regType,
                                    final boolean superUser) throws Throwable {
    Timestamp  sqlDate = new Timestamp(new java.util.Date().getTime());

    logger.debug("Event details: " + getCurrentUser() + " " +
        eventHref + " " +
        regType);

    /* we  let superusers register over and over, but not regular users */

    Registration reg;
    if (!superUser) {
      reg = db.getUserRegistration(eventHref, getCurrentUser());

      if (reg != null) {
        reg.setNumTickets(numTickets);
        reg.setComment(comment);
        reg.setType(regType);

        db.update(reg);

        return reg.getTicketid();
      }
    }

    /* Create new entry */

    reg = new Registration();

    reg.setAuthid(getCurrentUser());
    reg.setComment(comment);
    reg.setEventHref(eventHref);
    reg.setNumTickets(numTickets);
    reg.setCreated(sqlDate.toString());
    reg.setTicketid(UUID.randomUUID().toString());

    db.add(reg);

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
  public Registration getRegistrationById(final String id) throws Throwable {
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
    return db.getRegistrantCount(getCurrEvent().getHref());
  }

  /**
   * @return count of tickets allocated
   * @throws Throwable
   */
  public long getTicketCount() throws Throwable {
    return db.getTicketCount(getCurrEvent().getHref());
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
  public String getTicketId() {
    return getReqPar(reqparTicketId);
  }

  /**
   * @return comment or null for no parameter
   */
  public String getComment() {
    return getReqPar(reqparComment);
  }

  /**
   * @return type or null for no parameter
   */
  public String getType() {
    return getReqPar(reqparType);
  }

  private HashMap<String, Event> events = new HashMap<String, Event>();

  /**
   * @param reg
   * @return event referenced by registration
   * @throws Throwable
   */
  public Event retrieveEvent(final Registration reg) throws Throwable {
//    EventXMLParser ep = new EventXMLParser();
  //  ep.Parse(urltext);
    //Event ev = ep.getEvent();

    // events.put(reg.getHref(), ev);

    return null;
  }
}
