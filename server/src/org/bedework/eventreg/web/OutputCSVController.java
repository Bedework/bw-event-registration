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

import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class OutputCSVController extends AbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    if (!sessMan.getSuperUser()) {
      logger.info("Non superuser attempted access " + getClass().getName());
      return errorReturn("No access");
    }

    TreeSet<Registration> regs = new TreeSet<Registration>();

    for (Registration reg: sessMan.getAllRegistrations()) {
      reg.setEvent(sessMan.retrieveEvent(reg));

      regs.add(reg);
    }

    return objModel("csv", regs);
  }
}
