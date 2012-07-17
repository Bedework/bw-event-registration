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
import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 */
public class UpdateAdminRegController extends AdminAuthAbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    Long regId = sessMan.getRegistrationId();
    if (regId == null) {
      return errorReturn("No registration id supplied");
    }

    if (debug) {
      logger.debug("updating registration " + regId);
    }

    Registration reg = sessMan.getRegistrationById(regId);

    if (reg == null) {
      return errorReturn("No registration found.");
    }

    Event currEvent = sessMan.getCurrEvent();

    int numTickets = sessMan.getTicketsRequested();
    long allocated = sessMan.getRegTicketCount();
    int total = currEvent.getMaxTickets();
    long available = total - allocated;

    int toAllocate = (int)Math.min(numTickets, available);

    /*
    long newTotal = numTickets + allocated - reg.getNumTickets();

    if (newTotal > total) {
      logger.info("Registration is full");
      return errorReturn("Registration is full: you may only decrease or remove tickets.");
    }
    */

    reg.setTicketsRequested(numTickets);

    int released = reg.getNumTickets() - numTickets;

    if (released > 0) {
      reg.removeTickets(released);
      reallocate(released);
    } else if (toAllocate > 0) {
      reg.addTickets(toAllocate);
    }

    reg.setType(Registration.typeRegistered);
    reg.setComment(sessMan.getComment());
    sessMan.updateRegistration(reg);

    sessMan.setMessage("Registration number " + regId + " updated by admin.");

    return sessModel("forward:adminagenda.do");
  }
}
