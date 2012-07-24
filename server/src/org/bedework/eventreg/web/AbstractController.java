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

import org.bedework.eventreg.bus.ChangeManager;
import org.bedework.eventreg.bus.ChangeManager.ChangeItem;
import org.bedework.eventreg.bus.Request;
import org.bedework.eventreg.bus.SessionManager;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Event;
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
   * @param request
   * @param response
   * @return ModelAndView
   * @throws Throwable
   */
  public abstract ModelAndView doRequest(final HttpServletRequest request,
                                         final HttpServletResponse response) throws Throwable;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    try {
      ModelAndView mv = setup(request);

      if (mv != null) {
        return mv;
      }

      return doRequest(request, response);
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

  protected ModelAndView setup(final HttpServletRequest request) throws Throwable {
    debug = logger.isDebugEnabled();

    if (debug) {
      logger.debug("Entry: " + getClass().getSimpleName());
      dumpRequest(request);
    }

    req = sessMan.getReq();
    sessMan.setMessage("");

    return null;
  }

  protected ModelAndView sessModel(final String view) {
    Map<String, Object> myModel = new HashMap<String, Object>();
    myModel.put("sessMan", sessMan);
    myModel.put("req", req);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView objModel(final String view,
                                  final String name,
                                  final Object m) {
    Map<String, Object> myModel = new HashMap<String, Object>();
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
    Map<String, Object> myModel = new HashMap<String, Object>();
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
      Enumeration<String> names = req.getParameterNames();

      String title = "Request parameters";

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
        String key = names.nextElement();
        String[] vals = req.getParameterValues(key);
        for (String val: vals) {
          logger.debug("  " + key + " = \"" + val + "\"");
        }
      }
    } catch (Throwable t) {
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
    List<Registration> regs = sessMan.getWaiting(href);

    int remaining = numTickets;

    for (Registration reg: regs) {
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

  /** Adjust tickets for the current event - perhaps as a result of increasing
   * seats.
   * @throws Throwable
   */
  protected void adjustTickets() throws Throwable {
    Event currEvent = sessMan.getCurrEvent();

    long allocated = sessMan.getRegTicketCount();
    int total = currEvent.getMaxTickets();
    int available = (int)(total - allocated);

    if (available > 0) {
      return;
    }

    reallocate(available, req.getHref());
  }

  protected void adjustTickets(final Registration reg) throws Throwable {
    Event currEvent = sessMan.getCurrEvent();

    int numTickets = req.getTicketsRequested();
    if (numTickets < 0) {
      // Not setting tickets
      return;
    }

    /* change > 0 to add tickets, < 0 to release tickets */
    int change = numTickets - reg.getTicketsRequested();

    if (change == 0) {
      // Not changing anything
      return;
    }

    if (change > 0) {
      long allocated = sessMan.getRegTicketCount();
      int total = currEvent.getMaxTickets();

      /* Total number of available tickets - may be negative for over-allocated */
      long available = Math.max(0, total - allocated);

      /* The number to add to this registration */
      int toAllocate = (int)Math.min(change, available);

      if (toAllocate != change) {
        /* We should check the request par expectedAvailable to see if it changed
         * during this interaction. If it did we may have given the user stale
         * information.
         *
         * If so abandon this and represent the information to the user.
         */
      }

      change = toAllocate;
    }

    if ((reg.getWaitqDate() == null) ||
        (change > 0)) {
      reg.setWaitqDate();
    }

    reg.setTicketsRequested(numTickets);

    ChangeManager chgMan = sessMan.getChangeManager();

    if (change < 0) {
      reg.removeTickets(-change);
      chgMan.addChange(reg, Change.typeUpdReg,
                       ChangeItem.makeUpdNumTickets(change));
      reallocate(-change, reg.getHref());
    } else {
      reg.addTickets(change);

      chgMan.addChange(reg, Change.typeUpdReg,
                       ChangeItem.makeUpdNumTickets(change));
    }
  }
}
