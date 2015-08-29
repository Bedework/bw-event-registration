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

import org.bedework.eventreg.bus.Request;
import org.bedework.eventreg.bus.SessionManager;
import org.bedework.eventreg.db.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provide some useful common functionality.
 *
 */
public abstract class AbstractController implements Controller {
  protected final Log logger = LogFactory.getLog(getClass());

  protected SessionManager sessMan;

  protected Request req;

  private String forwardTo;

  protected boolean debug;

  /**
   * @return ModelAndView
   * @throws Throwable
   */
  public abstract ModelAndView doRequest() throws Throwable;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    try {
      ModelAndView mv = setup();

      if (mv != null) {
        return mv;
      }

      return doRequest();
    } catch (Exception e) {
      logger.error(this, e);

      return errorReturn(e);
    } catch (Throwable t) {
      logger.error(t);

      return errorReturn(t);
    } finally {
      if (!sessMan.closeDb()) {
        return errorReturn("Error during close");
      }
    }
  }

  protected ModelAndView setup() throws Throwable {
    debug = logger.isDebugEnabled();

    req = sessMan.getReq();
    sessMan.setMessage("");

    if (debug) {
      logger.debug("Entry: " + getClass().getSimpleName());
      dumpRequest(req.getRequest());
    }

    return null;
  }

  protected ModelAndView sessModel(final String view) {
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", sessMan);
    myModel.put("req", req);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView objModel(final String view,
                                  final String name,
                                  final Object m) {
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", sessMan);
    myModel.put("req", req);
    myModel.put(name, m);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView errorReturn(final Throwable t) {
    return errorReturn(t.getLocalizedMessage());
  }

  protected ModelAndView errorReturn(final String msg) {
    sessMan.setMessage(msg);
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", sessMan);
    myModel.put("req", req);

    return new ModelAndView("error", myModel);
  }

  /**
   * @param sm
   */
  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }

  /** Set by Spring
   * @param val
   */
  public void setForwardTo(final String val) {
    forwardTo = val;
  }

  /**
   * @return forward
   */
  public String getForwardTo() {
    return forwardTo;
  }

  /**
   * @param req
   */
  public void dumpRequest(final HttpServletRequest req) {
    try {
      @SuppressWarnings("unchecked")
      final Enumeration<String> names = req.getParameterNames();

      final String title = "Request parameters";

      logger.debug(title + " - global info and uris");
      logger.debug("getRequestURI = " + req.getRequestURI());
      logger.debug("getRemoteUser = " + req.getRemoteUser());
      logger.debug("getRequestedSessionId = " + req.getRequestedSessionId());
      logger.debug("HttpUtils.getRequestURL(req) = " + req.getRequestURL());
      logger.debug("query=" + req.getQueryString());
      logger.debug("contentlen=" + req.getContentLength());
      logger.debug("request=" + req);
      logger.debug("parameters:");

      logger.debug(title);

      while (names.hasMoreElements()) {
        final String key = names.nextElement();
        final String[] vals = req.getParameterValues(key);
        for (final String val: vals) {
          logger.debug("  " + key + " = \"" + val + "\"");
        }
      }
    } catch (final Throwable t) {
      logger.error(this, t);
    }
  }

  /** Allocate given number of tickets for the event to any waiters
   *
   * @param numTickets
   * @param href
   * @throws Throwable
   */
  protected void reallocate(final int numTickets,
                            final String href) throws Throwable {
    final List<Registration> regs = sessMan.getWaiting(href);

    int remaining = numTickets;

    for (final Registration reg: regs) {
      int required = reg.getTicketsRequested() - reg.getNumTickets();

      if (required <= 0) {
        continue;
      }

      required = Math.min(required, remaining);

      reg.addTickets(required);
      sessMan.updateRegistration(reg);
      sessMan.getChangeManager().addTicketsAdded(reg, required);

      if (reg.getTicketsRequested() == reg.getNumTickets()) {
        sessMan.getChangeManager().addRegFulfilled(reg);
      }

      remaining -= required;

      if (remaining <= 0) {
        break;
      }
    }
  }
}
