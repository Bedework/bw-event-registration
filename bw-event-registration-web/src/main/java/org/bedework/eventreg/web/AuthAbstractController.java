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

import org.bedework.eventreg.bus.ChangeManager;
import org.bedework.eventreg.bus.ChangeManager.ChangeItem;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

/**
 * Ensure user is authenticated.
 *
 */
public abstract class AuthAbstractController extends AbstractController {
  /** True for an administration action */
  protected boolean admin;

  @Override
  protected ModelAndView setup() {
    final ModelAndView mv = super.setup();

    if (mv != null) {
      return mv;
    }

    if (sessMan.getCurrentUser() == null) {
      return errorReturn("Not authenticated");
    }

    return null;
  }

  protected ModelAndView updateRegistration() {
    final Long regId = req.getRegistrationId();
    if (regId == null) {
      return errorReturn("No registration id supplied");
    }

    if (debug()) {
      debug("updating registration " + regId);
    }

    final Registration reg = sessMan.getRegistrationById(regId);

    if (reg == null) {
      return errorReturn("No registration found.");
    }

    if (!admin &&
        !reg.getAuthid().equals(sessMan.getCurrentUser())) {
      return errorReturn("You are not authorized to update that registration.");
    }

    adjustTickets(reg);

    if (admin) {
      reg.setComment(req.getComment());
    }

    sessMan.updateRegistration(reg);

    sessMan.setMessage("Registration number " + regId +
                       " updated: " + "admin: " + admin +
                       " user: " + sessMan.getCurrentUser());

    return null;
  }

  protected ModelAndView removeRegistration(final boolean admin) {
    final Long regId = req.getRegistrationId();
    if (regId == null) {
      return errorReturn("No registration id supplied");
    }

    if (debug()) {
      debug("remove reg id: " + regId +
                    ", user: " + sessMan.getCurrentUser());
    }

    final Registration reg = sessMan.getRegistrationById(regId);

    if (reg == null) {
      return errorReturn("No registration found.");
    }

    if (!admin &&
        !reg.getAuthid().equals(sessMan.getCurrentUser())) {
      return errorReturn("You are not authorized to remove that registration.");
    }

    reallocate(reg.getNumTickets(), reg.getHref());
    sessMan.removeRegistration(reg);
    sessMan.getChangeManager().deleteReg(reg);

    return null;
  }

  /** Adjust tickets for the current event - perhaps as a result of increasing
   * seats.
   */
  protected void adjustTickets() {
    final Event currEvent = sessMan.getCurrEvent();

    final long allocated = sessMan.getRegTicketCount();
    final int total = currEvent.getMaxTickets();
    final int available = (int)(total - allocated);

    if (available <= 0) {
      return;
    }

    reallocate(available, req.getHref());
  }

  protected enum AdjustResult {
    removed,
    added,
    nochange,     // No change to registration
    notickets,    // Not allocating tickets
    waitListFull  // Wait list is full
  }
  
  protected AdjustResult adjustTickets(final Registration reg) {
    final Event currEvent = sessMan.getCurrEvent();

    final int numTickets = req.getTicketsRequested();
    if (numTickets < 0) {
      // Not setting tickets
      return AdjustResult.notickets;
    }

    /* change > 0 to add tickets, < 0 to release tickets 
     * For a new registration  reg.getTicketsRequested() == 0
     * */
    int change = numTickets - reg.getTicketsRequested();

    if (change == 0) {
      // Not changing anything
      return AdjustResult.nochange;
    }

    /* For a non-admin user limit the change to the number available */
    if (!admin && (change > 0)) {
      /* First see if wait list is full
       */
      String waitListLimitVal = currEvent.getWaitListLimit();
      final int total = currEvent.getMaxTickets();

      final long allocated = sessMan.getRegTicketCount();

      /* Total number of available tickets - may be negative for over-allocated */
      final long available = Math.max(0, total - allocated);
      
      if (waitListLimitVal != null) {
        final boolean percentage = waitListLimitVal.endsWith("%");
        if (percentage) {
          waitListLimitVal =
                  waitListLimitVal.substring(0, 
                                             waitListLimitVal.length() - 1);
        }
        
        int waitListLimit = Integer.parseInt(waitListLimitVal.trim());
        if (percentage) {
          waitListLimit = total * waitListLimit / 100;
        }
        
        final long waiting = sessMan.getWaitingTicketCount();
        if ((available < change) && ((waiting + change - available) > waitListLimit)) {
          return AdjustResult.waitListFull;
        }
      }

      /* The number to add to this registration */
      final int toAllocate = (int)Math.min(change, available);

      if (toAllocate != change) {
        /* We should check the request par expectedAvailable to see if it changed
         * during this interaction. If it did we may have given the user stale
         * information.
         *
         * If so abandon this and represent the information to the user.
         */
      }

      change = toAllocate;
    }

    if ((reg.getWaitqDate() == null) ||
        (change > 0)) {
      reg.setWaitqDate();
    }

    reg.setTicketsRequested(numTickets);

    final ChangeManager chgMan = sessMan.getChangeManager();

    if (change < 0) {
      reg.removeTickets(-change);
      chgMan.addChange(reg, Change.typeUpdReg,
                       ChangeItem.makeUpdNumTickets(change));
      reallocate(-change, reg.getHref());

      return AdjustResult.removed;
    }

    reg.addTickets(change);

    chgMan.addChange(reg, Change.typeUpdReg,
                     ChangeItem.makeUpdNumTickets(change));
    return AdjustResult.added;
  }
}
