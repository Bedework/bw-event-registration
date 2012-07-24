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

import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Ensure user is authenticated.
 *
 */
public abstract class AuthAbstractController extends AbstractController {
  @Override
  protected ModelAndView setup(final HttpServletRequest request) throws Throwable {
    ModelAndView mv = super.setup(request);

    if (mv != null) {
      return mv;
    }

    if (sessMan.getCurrentUser() == null) {
      return errorReturn("Not authenticated");
    }

    return null;
  }

  protected ModelAndView updateRegistration(final boolean admin) throws Throwable {
    Long regId = req.getRegistrationId();
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

  protected ModelAndView removeRegistration(final boolean admin) throws Throwable {
    Long regId = req.getRegistrationId();
    if (regId == null) {
      return errorReturn("No registration id supplied");
    }

    if (debug) {
      logger.debug("remove reg id: " + regId +
                   ", user: " + sessMan.getCurrentUser());
    }

    Registration reg = sessMan.getRegistrationById(regId);

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
}
