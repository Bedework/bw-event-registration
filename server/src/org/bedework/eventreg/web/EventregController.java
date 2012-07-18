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
package org.bedework.eventreg.web;

import org.bedework.eventreg.bus.SessionManager.ChangeItem;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class EventregController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    Event ev = sessMan.getCurrEvent();

    int maxTicketsAllowed = ev.getMaxTickets();
    if (maxTicketsAllowed < 0) {
      return errorReturn("Cannot register for this event.");
    }

    Date end = ev.getRegistrationEndDate();

    if (end == null) {
      return errorReturn("Application register: missing end date.");
    }

    sessMan.setDeadlinePassed(new Date().after(end));

    if (sessMan.getDeadlinePassed()) {
      logger.debug("event registration stop - deadline has passed");
      return errorReturn("Cannot register for this event - deadline has passed");
    }

    if (sessMan.getRegistrationFull()) {
      logger.debug("event registration stop - registration is full");
      return errorReturn("Cannot register for this event. Registration is full.");
    }

    String comment = sessMan.getComment();

    registerUserInEvent(comment);

    return sessModel("eventreg");
  }

  /**
   * @param comment
   * @return ticketId for registration
   * @throws Throwable
   */
  public Long registerUserInEvent(final String comment) throws Throwable {
    String href = sessMan.getHref();

    if (debug) {
      logger.debug("Event details: " + sessMan.getCurrentUser() + " " +
          href);
    }

    /* we  let adminUsers register over and over, but not regular users */

    Timestamp sqlDate = new Timestamp(new java.util.Date().getTime());

    Registration reg = sessMan.getRegistration();

    if (reg != null) {
      reg.setLastmod(sqlDate.toString());
      adjustTickets(reg);
      reg.setComment(comment);

      sessMan.updateRegistration(reg);

      sessMan.addChange(reg, Change.typeUpdReg,
                        ChangeItem.makeUpdNumTickets(reg.getTicketsRequested()));

      return reg.getRegistrationId();
    }

    /* Create new entry */

    reg = new Registration();

    reg.setAuthid(sessMan.getCurrentUser());
    reg.setComment(comment);
    reg.setType(Registration.typeRegistered);
    reg.setHref(href);
    reg.setRegistrationId(sessMan.getNextRegistrationId());

    reg.setCreated(sqlDate.toString());
    reg.setLastmod(reg.getCreated());

    adjustTickets(reg);

    sessMan.addRegistration(reg);

    sessMan.addChange(reg, Change.typeNewReg);

    return reg.getRegistrationId();
  }
}
