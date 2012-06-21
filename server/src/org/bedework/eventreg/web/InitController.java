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

import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.spring.bus.EventXMLParser;
import org.bedework.eventreg.spring.bus.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class InitController implements Controller {
  /** */
  public final static String EVENTINFOURL = "http://events.rpi.edu/event/eventView.do";
  protected final Log logger = LogFactory.getLog(getClass());

  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    try {
      logger.debug("Init Controller entry");

      String queryString = request.getQueryString();
      sessMan.setMessage("");

      if (queryString != null) {
        logger.debug("Init Controller  - query string: " +
            EVENTINFOURL +
            "?" +
            queryString +
            "&skinName=empacreg");

        String urltext;

        urltext = sessMan.getURL(EVENTINFOURL +
                                 "?" +
                                 queryString +
            "&skinName=empacreg");

        logger.debug("Init Controller  -  got event xml ");

        EventXMLParser ep = new EventXMLParser();
        ep.Parse(urltext);
        Event currEvent = ep.getEvent();
        currEvent.setQuery(queryString);
        sessMan.setCurrEvent(currEvent);
        currEvent.setUid(request.getParameter("guid"));
      } else {
        logger.debug("Init Controller  - getting event from session");
        if (sessMan.getCurrEvent() == null) {
          logger.warn("Init Controller  - could not get event!");
          return new ModelAndView("error");
        }
      }

      /* Set registrationFull to true or false */
      int maxRegistrants = sessMan.getCurrEvent().getMaxRegistrants();
      long curRegistrants = sessMan.getTicketCount();
      logger.debug("maxRegistrants: " + maxRegistrants);
      logger.debug("curRegistrants: " + curRegistrants);
      sessMan.setRegistrationFull(curRegistrants >= maxRegistrants);

      DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date deadline = formatter.parse(sessMan.getCurrEvent().getRegDeadline());
      Date now = new Date();
      if (now.before(deadline)) {
        sessMan.setDeadlinePassed(false);
      } else {
        sessMan.setDeadlinePassed(true);
      }

      Map myModel = new HashMap();

      myModel.put("sessMan", sessMan);
      return new ModelAndView("init", myModel);
    } catch (Exception e) {
      logger.error(this, e);
      throw e;
    } catch (Throwable t) {
      logger.info(t);
      sessMan.setMessage(t.getMessage());
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
