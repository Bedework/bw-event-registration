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
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class InitHandlers extends HandlerInterceptorAdapter
        implements Logged {
  private SessionManager sessMan;

  /**
   * @param sm session manager
   */
  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }

  @Override
  public boolean preHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler) {
    if (debug()) {
      debug("init handler Intercepter");
    }

    if (!sessMan.setReq(new Request(request, response))) {
      return false;
    }
    sessMan.openDb();

    return true;
  }

  /* ==========================================================
   *                   Logged methods
   * ========================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
