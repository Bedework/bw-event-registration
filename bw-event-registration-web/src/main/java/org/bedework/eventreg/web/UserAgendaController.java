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

import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import java.util.TreeSet;

/**
 * @author douglm
 *
 */
public class UserAgendaController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() {
    final TreeSet<Registration> regs = new TreeSet<>();

    for (final Registration reg:
            getSessMan().getRegistrationsByUser(getSessMan().getCurrentUser())) {
      final Event ev = getSessMan().retrieveEvent(reg);

      if ((ev == null) ||  // TODO Event deleted - should delete registration?
          (ev.getMaxTickets() < 0)) {
        // XXX Warn? - not registrable any more
        continue;
      }

      reg.setEvent(ev);

      regs.add(reg);
    }

    return objModel("agenda", "regs", regs);
  }
}
