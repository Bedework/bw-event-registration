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

import org.bedework.eventreg.spring.db.EmpacregDao;
import org.bedework.eventreg.spring.db.EventregDb;
import org.bedework.eventreg.spring.db.Registration;
import org.bedework.eventreg.spring.db.SysInfo;

import edu.rpi.sss.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class SessionManager  {
  protected final Log logger = LogFactory.getLog(getClass());

  private EventregDb db;
  private EmpacregDao dao;

  private String currentUser;
  private boolean superUser;

  private HttpServletRequest request;

  /** Request parameter - number of tickets
   */
  public static final String reqparNumtickets = "numtickets";

  private SysInfo sys;
  private Event currEvent;
  private boolean deadlinePassed;
  private boolean registrationFull;
  private String lastStatusMessage = "";

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

  public void setCurrentUser(final String val) {
    currentUser = val;
  }

  public String getCurrentUser() {
    return currentUser;
  }

  public boolean getSuperUser() {
    return superUser;
  }

  public void setCurrEvent(final Event currEvent) {
    this.currEvent = currEvent;
  }

  public Event getCurrEvent() {
    return  this.currEvent;
  }

  public void removeTicketById(final int id) throws Throwable {
    try {
      dao.removeTicketById(id,uif.getEmail(),uif.getType());
    }
    catch (Throwable t) {
      logger.info(t);
      setMessage(t.getMessage());
    }
  }

  public void updateTicketById(final int id, final int numTickets,
                               final String comment,
                               final String type) throws Throwable {
    try {
      dao.updateTicketById(id,uif.getEmail(),numTickets,comment,type,uif.getType());
    }
    catch (Throwable t) {
      logger.info(t);
      setMessage(t.getMessage());
    }
  }

  public void setEventGUID(final String eventGUID) {
    this.currEvent.setEventGUID(eventGUID);
  }

  public String getURL(final String url) throws Throwable {
    return URLReader.read(url);
  }

  public int registerUserInEvent(final int numTickets,
                                 final String comment,
                                 final String regType,
                                 final boolean superUser) {
    dao.registerUserInEvent(uif.getEmail(),
                            this.currEvent,
                            numTickets,
                            comment,
                            regType,
                            superUser);
    return 0;
  }

  public boolean validateEmail(final String activationCode) {
    return dao.validateEmail(activationCode);
  }

  public List<Map> getUserRegistrations(final String email) {
    return dao.getUserRegistrations(email);
  }

  /**
   * @return all the current registrations
   * @throws Throwable
   */
  public List<Registration> getAllRegistrations() throws Throwable {
    return db.getAllRegistrations();
  }

  public List getRegistrations(final String eventGUID) {
    return dao.getRegistrations(eventGUID);
  }

  public boolean getRegistrationFull() {
    return registrationFull;
  }

  public void setRegistrationFull(final boolean registrationFull) {
    this.registrationFull = registrationFull;
  }

  public int getRegistrantCount() {
    return dao.getRegistrantCount(this.currEvent.getEventGUID());
  }

  public int getTicketCount() {
    return dao.getTicketCount(this.currEvent.getEventGUID());
  }

  public int getUserTicketCount() {
    return dao.getUserTicketCount(this.currEvent.getEventGUID(),this.uif.getEmail());
  }

  public boolean getDeadlinePassed() {
    return deadlinePassed;
  }

  public void setDeadlinePassed(final boolean deadlinePassed) {
    this.deadlinePassed = deadlinePassed;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public void resendConfirmationEmail(final int userid) {
    String [] recipients = new String [1];
    recipients[0] = this.getUserInfo().getEmail();

    String body = "Thank you for registering to reserve tickets for EMPAC Opening events.";
        body = body.concat("\n\nJust click this link to confirm your email address," );
        body = body.concat("\nand you can start reserving tickets:\nhttp://reg.empac.rpi.edu/empacreg/confirm.do?activationCode="+this.getUserInfo().getGUID());
        body = body.concat("\n\nSee you there!\n-EMPAC\n\nQuestions? Call us @ 518.276.3921" );
        body = body.concat("\n\nExperimental Media and Performing Arts Center" );
        body = body.concat("\nRensselaer Polytechnic Institute" );
        body = body.concat("\n110 8th street" );
        body = body.concat("\nTroy, NY 12180" );
        body = body.concat("\nhttp://empac.rpi.edu" );

        /*    body = body.concat("Tickets will be available at will call on the day of the performance.\n");
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
    body = body.concat("http://empac.rpi.edu\n");*/

        try {
          Mailer.postMail(recipients, "EMPAC Registration", body, "empacboxoffice@rpi.edu", this.getUserInfo().getGUID());
        } catch (Exception e) {}

  }

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
}
