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

import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.web.EvregUserMethodHelper;

import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class ProcessShowRegistrations extends EvregUserMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    try (final var db = getEventregDb()) {
      db.open();
      final TreeSet<Registration> regs = new TreeSet<>();

      for (final var reg:
              db.getByUser(globals.getCurrentUser())) {
        final var href = reg.getHref();
        final var ev = getConnector().flush(href)
                                     .getEvent(href);

        if ((ev == null) ||  // TODO Event deleted - should delete registration?
                (ev.getMaxTickets() < 0)) {
          // XXX Warn? - not registrable any more
          continue;
        }

        reg.setEvent(ev);

        regs.add(reg);
      }

      setSessionAttr("regs", regs);
      forward("success");
    }
  }
}
