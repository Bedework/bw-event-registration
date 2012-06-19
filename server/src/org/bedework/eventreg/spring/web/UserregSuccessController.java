package org.bedework.eventreg.spring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.support.PagedListHolder;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.bedework.eventreg.spring.bus.Event;
import org.bedework.eventreg.spring.bus.SessionManager;
import org.bedework.eventreg.spring.db.SysInfo;


public class UserregSuccessController implements Controller {

    protected final Log logger = LogFactory.getLog(getClass());
    private SessionManager sessMan;

    public void setSessionManager(SessionManager sm) {
        sessMan = sm;
      }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("userregSuccess");
    }

}
