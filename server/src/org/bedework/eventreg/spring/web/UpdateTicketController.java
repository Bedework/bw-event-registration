package org.bedework.eventreg.spring.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.support.PagedListHolder;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.WebUtils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.bedework.eventreg.spring.bus.Event;
import org.bedework.eventreg.spring.bus.SessionManager;
import org.bedework.eventreg.spring.db.SysInfo;


public class UpdateTicketController implements Controller {

    protected final Log logger = LogFactory.getLog(getClass());
    private SessionManager sessMan;

    public void setSessionManager(SessionManager sm) {
        sessMan = sm;
      }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int ticketId = Integer.parseInt(request.getParameter("id"));
        int numTickets = Integer.parseInt(request.getParameter("qty"));

        String comment = request.getParameter("comment");
        if (comment == null) {
            comment = "";
        }

        String type = request.getParameter("type");
        if (type == null || type.equals("")) {
            type = "normal";
        }

        Event currEvent = sessMan.getCurrEvent();
        String userType = sessMan.getUserInfo().getType();
        if (userType == null || userType.equals("")) {
            userType = "confirmed";
        }
        logger.info("update ticket - user type: " + userType);

        if (numTickets + sessMan.getTicketCount() <= currEvent.getTotalRegistrants() ||
            numTickets < sessMan.getUserTicketCount() ||
            userType.equals("superuser")) {
            try {
                sessMan.updateTicketById(ticketId,numTickets,comment,type);
                sessMan.setMessage("Ticket number " + ticketId + " updated.");
            } catch (Throwable t) {
                logger.info(t);
                sessMan.setMessage(t.getMessage());
            }
        } else {
            logger.info("registration is full");
            sessMan.setMessage("Registration is full: you may only decrease or remove tickets.");
        }

        if (userType.equals("superuser")) {
            return new ModelAndView("forward:suagenda.do");
        } else {
            return new ModelAndView("forward:agenda.do");
        }
    }

}
