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
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** We will get at least the required fields.
 *
 * <p>We also may get a form name, a calsuite and a bunch of extra
 * fields. Those fields must be as specified by the form.</p>
 *
 * @author douglm
 *
 */
public class EventregController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() throws Throwable {
    final Event ev = sessMan.getCurrEvent();

    final int maxTicketsAllowed = ev.getMaxTickets();
    if (maxTicketsAllowed < 0) {
      return errorReturn("Cannot register for this event.");
    }

    final Date end = ev.getRegistrationEndDate();

    if (end == null) {
      return errorReturn("Application register: missing end date.");
    }

    sessMan.setDeadlinePassed(new Date().after(end));

    if (sessMan.getDeadlinePassed()) {
      if (debug) {
        debug("event registration stop - deadline has passed");
      }
      return errorReturn(
              "Cannot register for this event - deadline has passed");
    }

    registerUserInEvent(ev);

    return sessModel("eventreg");
  }

  /**
   * @return ticketId for registration
   * @throws Throwable
   */
  public Long registerUserInEvent(final Event ev) throws Throwable {
    final String href = req.getHref();

    if (debug) {
      debug("Event details: " + sessMan.getCurrentUser() + " " +
                    href);
    }

    /* we let adminUsers register over and over, but not regular users */

    Registration reg = sessMan.getRegistration();

    if (reg != null) {
      reg.setLastmod();
      adjustTickets(reg);
      reg.setComment(req.getComment());

      sessMan.updateRegistration(reg);

      return reg.getRegistrationId();
    }

    /* Create new entry */

    reg = new Registration();

    reg.setAuthid(sessMan.getCurrentUser());
    reg.setComment(req.getComment());
    reg.setType(Registration.typeRegistered);
    reg.setHref(href);
    reg.setRegistrationId(sessMan.getNextRegistrationId());

    String email = req.getEmail();

    if (email == null) {
      email = sessMan.getCurrEmail();
    }

    if (debug) {
      debug("req.email=" + req.getEmail() + " current=" + email);
    }
    reg.setEmail(email);

    reg.setEvSummary(ev.getSummary());
    reg.setEvDate(ev.getDate());
    reg.setEvTime(ev.getTime());
    reg.setEvLocation(ev.getLocation());

    reg.setTimestamps();

    adjustTickets(reg);

    handleFormInfo(reg);

    sessMan.addRegistration(reg);

    sessMan.getChangeManager().addChange(reg, Change.typeNewReg);

    return reg.getRegistrationId();
  }

  /* return null for ok - otherwise error message */
  private String handleFormInfo(final Registration reg) throws Throwable {
    final String calsuite = sessMan.getCurrentCalsuite();
    if (calsuite == null) {
      return "No calsuite";
    }

    final String formName = sessMan.getCurrentFormName();

    final FormDef form = sessMan.getFormDef(formName);

    if (form == null) {
      return "Form " + formName + " does not exist";
    }

    reg.setFormOwner(calsuite);
    reg.setFormName(formName);

    final FormFields ffs = new FormFields(form.getFields());

    final List<FieldDef> required = ffs.getRequiredFormEls();

    final Enumeration params = req.getRequest().getParameterNames();

    final Map<String, Object> vals = new HashMap<>();

    while (params.hasMoreElements()) {
      final String param = (String)params.nextElement();

      final FieldDef fd = ffs.getField(param);

      if (fd == null) {
        continue;
      }

      if (fd.getRequired()) {
        required.remove(fd);
      }

      // TODO Should check type here

      final String[] val = req.getRequest().getParameterValues(param);

      if (val.length == 0) {
        if (fd.getRequired()) {
          return "Missing required field(s)";
        }

        continue;
      }

      if (val.length > 1) {
        if (!fd.getMultivalued()) {
          return "Invalid field (multi)";
        }

        vals.put(param, val);
      } else {
        vals.put(param, val[0]);
      }
    }

    reg.saveFormValues(vals);

    return null;
  }
}
