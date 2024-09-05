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

import org.bedework.eventreg.bus.FormFields;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.FormDef;

import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @author douglm
 *
 */
public class InitController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() {
    sessMan.flushCurrEvent();
    final Event ev = sessMan.getCurrEvent();

    if (ev == null) {
      return errorReturn("Cannot retrieve the event.");
    }

    /* Set registrationFull to true or false */
    final int maxTickets = ev.getMaxTickets();
    if (maxTickets < 0) {
      return errorReturn("Cannot register for this event.");
    }

    final long curTickets = sessMan.getRegTicketCount();
    if (debug()) {
      debug("maxTickets: " + maxTickets);
      debug("curTickets: " + curTickets);
    }

    sessMan.setRegistrationFull(curTickets >= maxTickets);

    final Date end = ev.getRegistrationEndDate();

    if (end == null) {
      return errorReturn("Application register: missing end date.");
    }

    sessMan.setDeadlinePassed(new Date().after(end));

    if (!req.formNamePresent()) {
      if (debug()) {
        debug("No form specified");
      }
      return sessModel("init");
    }

    final String formName = req.getFormName();
    final FormDef form = sessMan.getFormDef(formName);

    if (sessMan.getCurrentCalsuite() == null) {
      return errorReturn("No calsuite");
    }

    if (form == null) {
      warn("Form " + formName + "does not exist");

      return sessModel("init");
    }

    sessMan.setCurrentFormName(formName);
    if (debug()) {
      debug("Set form name " + formName +
                    " for form with " + form.getFields().size() +
                    " fields");
    }

    return objModel("init", "form", form,
                    "fields", new FormFields(form.getFields()));
  }
}
