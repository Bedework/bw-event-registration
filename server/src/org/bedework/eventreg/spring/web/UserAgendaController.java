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

import org.bedework.eventreg.spring.bus.Event;
import org.bedework.eventreg.spring.bus.EventXMLParser;
import org.bedework.eventreg.spring.bus.SessionManager;
import org.bedework.eventreg.spring.db.SysInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserAgendaController implements Controller {
  protected final Log logger = LogFactory.getLog(getClass());
  public final static String EVENTINFOURL = "http://events.rpi.edu/event/eventView.do";

  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    logger.debug("UserAgendaController entry");
    SysInfo uif = sessMan.getUserInfo();

    logger.debug("UserAgendaController - " + uif.getEmail());

    TreeMap userRegistrationTreeMap = new TreeMap();

    for (Map currEvent: sessMan.getUserRegistrations(uif.getEmail())) {
      String urltext;

      try {
        urltext = sessMan.getURL(EVENTINFOURL +
                                 "?" +
                                 currEvent.get("queryStr") +
                                 "&skinName=empacreg");
      } catch (Throwable t) {
        logger.error(this, t);
        throw new Exception(t);
      }

      EventXMLParser ep = new EventXMLParser();
      ep.Parse(urltext);
      Event eventInfo = ep.getEvent();
      HashMap eventMap = new HashMap();
      eventMap.put("eventGUID",eventInfo.getEventGUID());
      eventMap.put("eventDateStr",eventInfo.getEventDateStr());
      eventMap.put("eventTimeStr",eventInfo.getEventTimeStr());
      eventMap.put("eventLocation",eventInfo.getEventLocation());
      eventMap.put("eventSummary",eventInfo.getEventSummary());
      eventMap.put("ticketsRequested",currEvent.get("numtickets"));
      eventMap.put("ticketsAllowed",eventInfo.getTicketsAllowed());
      eventMap.put("ticketId",currEvent.get("id"));
      userRegistrationTreeMap.put(eventInfo.getUtcStr(),eventMap);
    }

    ArrayList userRegistrationsFull = new ArrayList(userRegistrationTreeMap.values());

    Map myModel = new HashMap();
    myModel.put("userAgenda", userRegistrationsFull);
    myModel.put("sessMan", sessMan);

    return new ModelAndView("agenda", myModel);
  }

  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }
}
