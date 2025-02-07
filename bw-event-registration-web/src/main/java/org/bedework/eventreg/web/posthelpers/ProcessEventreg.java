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
package org.bedework.eventreg.web.posthelpers;

import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.Mailer;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.EventregUserDb;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.FormFields;
import org.bedework.eventreg.db.RegistrationImpl;
import org.bedework.eventreg.web.EvregUserMethodHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** We will get at least the required fields.
 *
 * <p>We also may get a form name, a calsuite and a bunch of extra
 * fields. Those fields must be as specified by the form.</p>
 *
 * @author douglm
 *
 */
public class ProcessEventreg extends EvregUserMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    try (final var db = getEventregDb()) {
      db.open();

      final Event ev = globals.getCurrentEvent();

      final int maxTicketsAllowed = ev.getMaxTickets();
      if (maxTicketsAllowed < 0) {
        errorReturn("Cannot register for this event. " +
                            "Allocated all tickets");
        return;
      }

      final Date end = ev.getRegistrationEndDate();

      if (end == null) {
        warn("Registration end time is null");
        errorReturn("Registration failure: missing end date.");
        return;
      }

      globals.setDeadlinePassed(new Date().after(end));

      if (globals.getDeadlinePassed()) {
        if (debug()) {
          debug("event registration stop - deadline has passed");
        }
        errorReturn(
                "Cannot register for this event - deadline has passed");
        return;
      }

      if (!registerUserInEvent(db, ev)) {
        if (debug()) {
          debug("event registration stop - waitlist is full");
        }
        errorReturn(
                "Cannot register for this event - waitlist is full");
        return;
      }
    }
    forward("success");
  }

  /**
   * @return false if event full
   */
  private boolean registerUserInEvent(final EventregUserDb db,
                                      final Event ev) {
    if (!requireHref()) {
      return false;
    }
    final String href = globals.getHref();

    if (debug()) {
      debug("Event details: " + globals.getCurrentUser() + " " +
                    href);
    }

    /* we let adminUsers register over and over, but not regular users */

    final var curReg =
            db.getUserRegistration(href,
                                   globals.getCurrentUser());
    if (curReg != null) {
      curReg.setLastmod();
      adjustTickets(db, curReg, false);
      curReg.setComment(reqComment());

      updateRegistration(db, false);

      return true;
    }

    /* Create new entry */

    final var reg = db.getNewRegistration();

    reg.setAuthid(globals.getCurrentUser());
    reg.setComment(reqComment());
    reg.setType(RegistrationImpl.typeRegistered);
    reg.setHref(href);
    reg.setRegistrationId(db.getNextRegistrationId());

    String email = reqEmail();

    if (email == null) {
      email = globals.getCurrentEmail();
    }

    if (email == null) {
      email = Mailer.getEmail(globals.getCurrentUser(),
                              emb.getConfig().getDefaultEmailDomain());
      globals.setCurrentEmail(email);
    }

    if (debug()) {
      debug("req.email=" + reqEmail() + " current=" + email);
    }
    reg.setEmail(email);

    reg.setEvSummary(ev.getSummary());
    reg.setEvDate(ev.getDate());
    reg.setEvTime(ev.getTime());
    reg.setEvLocation(ev.getLocation());

    reg.setTimestamps();

    if (adjustTickets(db, reg, false) == AdjustResult.waitListFull) {
      return false;
    }

    if (!handleFormInfo(db, reg)) {
      return false;
    }

    db.add(reg);

    addChange(db, reg, Change.typeNewReg);

    return true;
  }

  /* return null for ok - otherwise error message */
  private boolean handleFormInfo(final EventregUserDb db,
                                 final Registration reg) {
    if (!requireCalsuite() || !requireFormName()) {
      return false;
    }

    final String calsuite = globals.getCalsuite();
    final String formName = globals.getFormName();
    final FormDef form = getCalSuiteForm();

    if (form == null) {
      return false;
    }

    reg.setFormOwner(calsuite);
    reg.setFormName(formName);

    final var ffs = new FormFields(form.getFields());
    final var required = ffs.getRequiredFormEls();
    final var params =
            getRequest().getRequest().getParameterNames();
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

      final String[] val = getRequest().getRequest()
                                       .getParameterValues(param);

      if (val.length == 0) {
        if (fd.getRequired()) {
          errorReturn("Missing required field(s)");
          return false;
        }

        continue;
      }

      if (val.length > 1) {
        if (!fd.getMultivalued()) {
          errorReturn("Invalid field (multi)");
          return false;
        }

        vals.put(param, val);
      } else {
        vals.put(param, val[0]);
      }
    }

    reg.saveFormValues(vals);

    return true;
  }
}
