package org.bedework.eventreg.spring.web;

import org.bedework.eventreg.spring.bus.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfirmEmailRegistrationController implements Controller {
  public final static String EVENTINFOURL = "http://events.rpi.edu/event/eventView.do";
  protected final Log logger = LogFactory.getLog(getClass());

  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    logger.info("ConfirmEmailRegistrationController entry");
    //String queryString = request.getQueryString();
    String activationCode = request.getParameter("activationCode");
    if (activationCode != null) {
      logger.info("ConfirmEmailRegistrationController");
      sessMan.validateEmail(activationCode);
      return new ModelAndView("userregSuccess");

    } else {
      return new ModelAndView("error");
    }
  }

  public void setSessionManager(final SessionManager sm) {
	    sessMan = sm;
  }
}
