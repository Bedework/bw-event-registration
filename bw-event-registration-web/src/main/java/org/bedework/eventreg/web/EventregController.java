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
import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.RegistrationImpl;

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
  public ModelAndView doRequest() {
    final Event ev = getSessMan().getCurrEvent();

    final int maxTicketsAllowed = ev.getMaxTickets();
    if (maxTicketsAllowed < 0) {
      return errorReturn("Cannot register for this event.");
    }

    final Date end = ev.getRegistrationEndDate();

    if (end == null) {
      return errorReturn("Application register: missing end date.");
    }

    getSessMan().setDeadlinePassed(new Date().after(end));

    if (getSessMan().getDeadlinePassed()) {
      if (debug()) {
        debug("event registration stop - deadline has passed");
      }
      return errorReturn(
              "Cannot register for this event - deadline has passed");
    }

    if (!registerUserInEvent(ev)) {
      if (debug()) {
        debug("event registration stop - waitlist is full");
      }
      return errorReturn(
              "Cannot register for this event - waitlist is full");
    }

    return sessModel("eventreg");
  }

  /**
   * @return false if event full
   */
  private boolean registerUserInEvent(final Event ev) {
    final String href = req.getHref();

    if (debug()) {
      debug("Event details: " + getSessMan().getCurrentUser() + " " +
                    href);
    }

    /* we let adminUsers register over and over, but not regular users */

    Registration reg = getSessMan().getRegistration();

    if (reg != null) {
      reg.setLastmod();
      adjustTickets(reg);
      reg.setComment(req.getComment());

      getSessMan().updateRegistration(reg);

      return true;
    }

    /* Create new entry */

    reg = getSessMan().getNewRegistration();

    reg.setAuthid(getSessMan().getCurrentUser());
    reg.setComment(req.getComment());
    reg.setType(RegistrationImpl.typeRegistered);
    reg.setHref(href);
    reg.setRegistrationId(getSessMan().getNextRegistrationId());

    String email = req.getEmail();

    if (email == null) {
      email = getSessMan().getCurrEmail();
    }

    if (debug()) {
      debug("req.email=" + req.getEmail() + " current=" + email);
    }
    reg.setEmail(email);

    reg.setEvSummary(ev.getSummary());
    reg.setEvDate(ev.getDate());
    reg.setEvTime(ev.getTime());
    reg.setEvLocation(ev.getLocation());

    reg.setTimestamps();

    if (adjustTickets(reg) == AdjustResult.waitListFull) {
      return false;
    }

    handleFormInfo(reg);

    getSessMan().addRegistration(reg);

    getSessMan().getChangeManager().addChange(reg, Change.typeNewReg);

    return true;
  }

  /* return null for ok - otherwise error message */
  private String handleFormInfo(final Registration reg) {
    final String calsuite = getSessMan().getCurrentCalsuite();
    if (calsuite == null) {
      return "No calsuite";
    }

    final String formName = getSessMan().getCurrentFormName();

    final FormDef form = getSessMan().getFormDef(formName);

    if (form == null) {
      return "Form " + formName + " does not exist";
    }

    reg.setFormOwner(calsuite);
    reg.setFormName(formName);

    final FormFields ffs = new FormFields(form.getFields());

    final List<FieldDef> required = ffs.getRequiredFormEls();

    final Enumeration<String> params = req.getRequest().getParameterNames();

    final Map<String, Object> vals = new HashMap<>();

    while (params.hasMoreElements()) {
      final String param = params.nextElement();

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
