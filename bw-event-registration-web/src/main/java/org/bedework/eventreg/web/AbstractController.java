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
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

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
public abstract class AbstractController
        implements Logged, Controller {
  private SessionManager sessMan;

  protected Request req;

  private String forwardSuccess;

  private String forwardFail;

  private String forwardTo;

  /**
   * @return ModelAndView
   */
  public abstract ModelAndView doRequest();

  @Override
  public ModelAndView handleRequest(
          final HttpServletRequest request,
          final HttpServletResponse response) {
    try {
      final ModelAndView mv = setup();

      if (mv != null) {
        return mv;
      }

      return doRequest();
    } catch (final Throwable t) {
      error(t);

      return errorReturn(t);
    } finally {
      if (!getSessMan().closeDb()) {
        errorReturn("Error during close");
      }
    }
  }

  protected ModelAndView setup() {
    req = getSessMan().getReq();
    getSessMan().setMessage("");

    if (debug()) {
      debug("Entry: " + getClass().getSimpleName());
      dumpRequest(req.getRequest());
    }

    return null;
  }

  protected ModelAndView sessModel(final String view) {
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", getSessMan());
    myModel.put("req", req);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView objModel(final String view,
                                  final String name,
                                  final Object m) {
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", getSessMan());
    myModel.put("req", req);
    myModel.put(name, m);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView objModel(final String view,
                                  final String name,
                                  final Object m,
                                  final String name2,
                                  final Object m2) {
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", getSessMan());
    myModel.put("req", req);
    myModel.put(name, m);
    myModel.put(name2, m2);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView errorReturn(final Throwable t) {
    return errorReturn(t.getLocalizedMessage());
  }

  protected ModelAndView errorReturn(final String msg) {
    return errorReturn(getForwardFail(), msg);
  }

  protected ModelAndView errorReturn(final String forward,
                                     final String msg) {
    getSessMan().setMessage(msg);
    final Map<String, Object> myModel = new HashMap<>();
    myModel.put("sessMan", getSessMan());
    myModel.put("req", req);

    return new ModelAndView(forward, myModel);
  }

  /**
   * @param sm session manager
   */
  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }

  public SessionManager getSessMan() {
    return sessMan;
  }

  /** Set by Spring
   * @param val forward
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

  /** Set by Spring
   * @param val forward
   */
  public void setForwardSuccess(final String val) {
    forwardSuccess = val;
  }

  /**
   * @return forward
   */
  public String getForwardSuccess() {
    return forwardSuccess;
  }

  /** Set by Spring
   * @param val forward
   */
  public void setForwardFail(final String val) {
    forwardFail = val;
  }

  /**
   * @return forward
   */
  public String getForwardFail() {
    return forwardFail;
  }

  /**
   * @param req request
   */
  public void dumpRequest(final HttpServletRequest req) {
    try {
      final Enumeration<String> names = req.getParameterNames();

      final String title = "Request parameters";

      debug(title + " - global info and uris");
      debug("getRequestURI = " + req.getRequestURI());
      debug("getRemoteUser = " + req.getRemoteUser());
      debug("getRequestedSessionId = " + req.getRequestedSessionId());
      debug("HttpUtils.getRequestURL(req) = " + req.getRequestURL());
      debug("query=" + req.getQueryString());
      debug("contentlen=" + req.getContentLength());
      debug("request=" + req);
      debug("parameters:");

      debug(title);

      while (names.hasMoreElements()) {
        final String key = names.nextElement();
        final String[] vals = req.getParameterValues(key);
        for (final String val: vals) {
          debug("  " + key + " = \"" + val + "\"");
        }
      }
    } catch (final Throwable t) {
      error(t);
    }
  }

  /** Allocate given number of tickets for the event to any waiters
   *
   * @param numTickets number of tickets
   * @param href of event
   */
  protected void reallocate(final int numTickets,
                            final String href) {
    final List<Registration> regs = getSessMan().getWaiting(href);

    int remaining = numTickets;

    for (final Registration reg: regs) {
      int required = reg.getTicketsRequested() - reg.getNumTickets();

      if (required <= 0) {
        continue;
      }

      required = Math.min(required, remaining);

      reg.addTickets(required);
      getSessMan().updateRegistration(reg);
      getSessMan().getChangeManager().addTicketsAdded(reg, required);

      if (reg.getTicketsRequested() == reg.getNumTickets()) {
        getSessMan().getChangeManager().addRegFulfilled(reg);
      }

      remaining -= required;

      if (remaining <= 0) {
        break;
      }
    }
  }

  /* =========================================================
   *                   Logged methods
   * ========================================================= */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
