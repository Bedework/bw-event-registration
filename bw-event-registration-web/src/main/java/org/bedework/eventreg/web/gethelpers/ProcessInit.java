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
package org.bedework.eventreg.web.gethelpers;

import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.FormFields;
import org.bedework.eventreg.web.EvregUserMethodHelper;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class ProcessInit extends EvregUserMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    if (!requireHref() ||
            !requireCalsuite() ||
            !requireFormName()) {
      return;
    }

    final var href = globals.getHref();

    try (final var db = getEventregDb()) {
      db.open();

      final var ev = getConnector().flush()
                                   .getEvent(href);

      if (ev == null) {
        errorReturn("Cannot retrieve the event.");
        return;
      }

      /* Set registrationFull to true or false */
      final int maxTickets = ev.getMaxTickets();
      if (maxTickets < 0) {
        errorReturn("Cannot register for this event.");
        return;
      }

      final long curTickets = db.getRegTicketCount(href);
      if (debug()) {
        debug("maxTickets: " + maxTickets);
        debug("curTickets: " + curTickets);
      }

      globals.setRegistrationFull(curTickets >= maxTickets);

      final Date end = ev.getRegistrationEndDate();

      if (end == null) {
        errorReturn("Application register: missing end date.");
        return;
      }

      globals.setDeadlinePassed(new Date().after(end));

      final String formName = globals.getFormName();
      final FormDef form = getCalSuiteForm();

      if (form == null) {
        errorReturn("Form " + formName + "does not exist");
        return;
      }

      globals.setFormName(formName);
      if (debug()) {
        debug("Set form name " + formName +
                      " for form with " + form.getFields().size() +
                      " fields");
      }

      final var reg = db.getUserRegistration(href,
                                             globals.getCurrentUser());
      if (reg == null) {
        errorReturn("Application error: " +
                            "missing registration for href " +
                            href);
        return;
      }

      globals.setRegistration(reg);
      globals.setWaiting(reg.getNumTickets() <
                                 reg.getTicketsRequested());

      setSessionAttr("form", form);
      setSessionAttr("fields", new FormFields(form.getFields()));
      forward("success");
    }
  }
}
