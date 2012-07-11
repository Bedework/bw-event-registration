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

import org.bedework.eventreg.db.Event;

import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author johnsa
 *
 */
public class WaitingListController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    Event ev = sessMan.getCurrEvent();

    int maxTicketsAllowed = ev.getMaxTickets();
    if (maxTicketsAllowed < 0) {
      return errorReturn("Cannot register for this event.");
    }
    
    int numTicketsRequested = sessMan.getTicketsRequested();

    Date end = ev.getRegistrationEndDate();

    if (end == null) {
      return errorReturn("Application register: missing end date.");
    }

    sessMan.setDeadlinePassed(new Date().after(end));

    if (sessMan.getDeadlinePassed()) {
      logger.debug("event registration stop - deadline has passed");
      return errorReturn("Cannot register for this event - deadline has passed");
    }

    if ((numTicketsRequested > ev.getMaxTicketsPerUser()) &&
        !sessMan.getAdminUser()) {
      logger.debug("Number of tickets requested exceeds number of tickets allowed.");
      return errorReturn("Cannot register for this event - " +
      		"number of tickets requested exceeds number of tickets allowed.");
    }

    String regType = sessMan.typeWaiting;
    String comment = sessMan.getComment();

    logger.debug("waiting list registration  - number of tickets requested: " + numTicketsRequested);
    logger.debug("waiting list registration  - superuser: " + sessMan.getAdminUser());

    sessMan.registerUserInEvent(numTicketsRequested,
                                comment,
                                regType,
                                sessMan.getAdminUser());

    return sessModel("waitlist");
  }
}
