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
 * @author douglm
 *
 */
public class InitController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    sessMan.flushCurrEvent();
    Event ev = sessMan.getCurrEvent();

    if (ev == null) {
      return errorReturn("Cannot retrieve the event.");
    }

    /* Set registrationFull to true or false */
    int maxTickets = ev.getMaxTickets();
    if (maxTickets < 0) {
      return errorReturn("Cannot register for this event.");
    }

    long curTickets = sessMan.getRegTicketCount();
    if (debug) {
      logger.debug("maxTickets: " + maxTickets);
      logger.debug("curTickets: " + curTickets);
    }

    sessMan.setRegistrationFull(curTickets >= maxTickets);

    Date end = ev.getRegistrationEndDate();

    if (end == null) {
      return errorReturn("Application register: missing end date.");
    }

    sessMan.setDeadlinePassed(new Date().after(end));

    return sessModel("init");
  }
}
