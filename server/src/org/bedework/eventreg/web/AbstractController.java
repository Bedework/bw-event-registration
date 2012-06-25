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

import org.bedework.eventreg.bus.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashMap;
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
      logger.debug("Entry: " + this.getClass().getSimpleName());

      sessMan.setMessage("");

      return doRequest(request, response);
    } catch (Exception e) {
      logger.error(this, e);

      return errorReturn(e);
    } catch (Throwable t) {
      logger.error(t);

      return errorReturn(t);
    } finally {
      sessMan.closeDb();
    }
  }

  protected ModelAndView sessModel(final String view) {
    Map myModel = new HashMap();
    myModel.put("sessMan", sessMan);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView objModel(final String view, final Object m) {
    Map myModel = new HashMap();
    myModel.put("sessMan", sessMan);
    myModel.put("sessMan", m);

    return new ModelAndView(view, myModel);
  }

  protected ModelAndView errorReturn(final Throwable t) {
    return errorReturn(t.getLocalizedMessage());
  }

  protected ModelAndView errorReturn(final String msg) {
    sessMan.setMessage(msg);
    Map myModel = new HashMap();
    myModel.put("sessMan", sessMan);

    return new ModelAndView("error", myModel);
  }

  /**
   * @param sm
   */
  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }
}
