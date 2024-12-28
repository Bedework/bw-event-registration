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
package org.bedework.eventreg.webadmin.posthelpers;

import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.RegistrationImpl;
import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Hold tickets
 *
 */
public class ProcessHold extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    final var ev = globals.getCurrentEvent();
    final int numTickets = reqTicketsRequested();

    try (final var db = getEventregDb()) {
      db.open();

      final Registration reg = db.getNewRegistration();

      reg.setRegistrationId(db.getNextRegistrationId());
      reg.setAuthid(globals.getCurrentUser());
      reg.setHref(ev.getHref());
      reg.setTicketsRequested(numTickets);
      reg.addTickets(numTickets);
      reg.setType(RegistrationImpl.typeHold);
      reg.setComment(reqComment());
      reg.setTimestamps();

      db.add(reg);
    }

    forward("success");
  }
}
