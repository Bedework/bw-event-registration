package org.bedework.eventreg.spring.web;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.bedework.eventreg.spring.bus.Event;
import org.bedework.eventreg.spring.bus.EventXMLParser;
import org.bedework.eventreg.spring.bus.SessionManager;
import org.bedework.eventreg.spring.db.SysInfo;



import org.springframework.web.bind.ServletRequestUtils;


public class SuperUserAgendaController implements Controller {

  protected final Log logger = LogFactory.getLog(getClass());

  private SessionManager sessMan;

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    Event currEvent = sessMan.getCurrEvent();
    String userType = sessMan.getUserInfo().getType();

    logger.info("SuperUserAgendaController entry");

    if (userType.equals("superuser")) {
        List registrations = sessMan.getRegistrations(currEvent.getEventGUID());
        ListIterator li = registrations.listIterator();
        ArrayList registrationsFull = new ArrayList();

        while (li.hasNext()) {
          Map currReg = (Map)li.next();
          HashMap regMap = new HashMap();
          regMap.put("id",currReg.get("id"));
          regMap.put("fname",currReg.get("fname"));
          regMap.put("lname",currReg.get("lname"));
          regMap.put("email",currReg.get("email"));
          regMap.put("numtickets",currReg.get("numtickets"));
          regMap.put("type",currReg.get("type"));
          regMap.put("rpi",currReg.get("rpi"));
          regMap.put("comment",currReg.get("comment"));
          regMap.put("created_at",currReg.get("created_at"));
          regMap.put("updated_at",currReg.get("updated_at"));
          registrationsFull.add(regMap);
        }

        Map myModel = new HashMap();
        myModel.put("suserAgenda", registrationsFull);
        myModel.put("sessMan", sessMan);

        return new ModelAndView("suagenda", myModel);
    } else {
        logger.info("Non superuser attempted to access SuperUserAgenda.");
        return new ModelAndView("error");
    }
  }


  public void setSessionManager(SessionManager sm) {
    sessMan = sm;
  }


}
