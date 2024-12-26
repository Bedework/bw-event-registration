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

import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.Registration;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    final Event ev = getConnector().flush(globals.getHref())
                                   .getEvent(globals.getHref());

    final TreeSet<Registration> regs = new TreeSet<>();

    for (final var reg: getEventregDb().getByEvent(ev.getHref())) {
      reg.setEvent(ev);

      regs.add(reg);
    }

    setSessionAttr("regs", regs);
    forward("success");

    return objModel("adminagenda", "regs", regs);
  }
}
