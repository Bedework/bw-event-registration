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
package org.bedework.eventreg.spring.web;

import org.bedework.eventreg.db.Registration;
import org.bedework.eventreg.spring.bus.AllEventsXMLParser;
import org.bedework.eventreg.spring.bus.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class OutputCSVController implements Controller {
  /**   */
  public final static String EVENTINFOURL =
      "http://events.rpi.edu/main/listEvents.do" +
         "?start=2009-12-04" +
          "&end=2009-12-07" +
          "&setappvar=creator(agrp_EMPACSpecial)" +
          "&skinName=empacreg";

  protected final Log logger = LogFactory.getLog(getClass());

  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    logger.debug("OutputCSVController entry");

    if (!sessMan.getSuperUser()) {
      logger.info("Non superuser attempted to access OutputCSVController.");
      return new ModelAndView("error");
    }

    try {
      String urltext = sessMan.getURL(EVENTINFOURL);

      AllEventsXMLParser aep = new AllEventsXMLParser();
      aep.Parse(urltext);

      TreeSet<Registration> regs = new TreeSet<Registration>();

      for (Registration reg: sessMan.getAllRegistrations()) {
        reg.setEvent(sessMan.retrieveEvent(reg));

        regs.add(reg);
      }

      Map myModel = new HashMap();
      myModel.put("registrations", regs);

      return new ModelAndView("csv", myModel);
    } catch (Exception e) {
      logger.error(this, e);
      throw e;
    } catch (Throwable t) {
      logger.error(this, t);
      throw new Exception(t);
    }
  }

  /**
   * @param sm
   */
  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }
}
