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
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class RemoveRegController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    Long regId = sessMan.getRegistrationId();
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

    if (!reg.getAuthid().equals(sessMan.getCurrentUser())) {
      return errorReturn("You are not authorized to remove that registration.");
    }

    sessMan.removeRegistration(reg);

    return sessModel(getForwardTo());
  }
}