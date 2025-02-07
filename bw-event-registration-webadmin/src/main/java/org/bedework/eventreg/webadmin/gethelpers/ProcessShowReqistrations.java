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
package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;

import java.util.List;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class ProcessShowReqistrations extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    if (!requireHref()) {
      return;
    }

    try (final var db = getEventregDb()) {
      db.open();

      final var href = globals.getHref();
      final var ev = getConnector().flush(href)
                                   .getEvent(href);

      final TreeSet<Registration> regs = new TreeSet<>();

      for (final var reg: db.getByEvent(href)) {
        reg.setEvent(ev);

        regs.add(reg);
      }

      globals.setCurrentEvent(ev);

      globals.setRegInfo(
              db.getRegistrationInfo(ev, href));

      setSessionAttr("regs", regs);
      forward("success");
    }
  }
}
