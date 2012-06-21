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

import org.bedework.eventreg.spring.bus.SessionManager;
import org.bedework.eventreg.spring.db.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class RemoveTicketController implements Controller {
  protected final Log logger = LogFactory.getLog(getClass());
  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {

    try {
      String ticketId = sessMan.getTicketId();

      logger.debug("remove ticket id: " + ticketId +
                   "- super user: " + sessMan.getSuperUser());

      Registration reg = sessMan.getRegistrationById(ticketId);

      if (reg == null) {
        // XXX message?
      } else if (!sessMan.getSuperUser() &&
          !reg.getAuthid().equals(sessMan.getCurrentUser())) {
        // XXX message?
      } else{
        sessMan.removeRegistration(reg);
      }
      sessMan.setMessage(""); // don't need to say anything

      if (sessMan.getSuperUser()) {
        return new ModelAndView("forward:suagenda.do");
      } else {
        return new ModelAndView("forward:agenda.do");
      }
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
