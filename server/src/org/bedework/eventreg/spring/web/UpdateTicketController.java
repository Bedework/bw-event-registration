package org.bedework.eventreg.spring.web;

import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.spring.bus.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author douglm
 */
public class UpdateTicketController implements Controller {
  protected final Log logger = LogFactory.getLog(getClass());
  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    try {
      String ticketId = sessMan.getTicketId();
      int numTickets = sessMan.getTicketsRequested();
      String comment = sessMan.getComment();
      String type = sessMan.getType();

      if (type == null) {
        type = "normal";
      }

      Event currEvent = sessMan.getCurrEvent();

      logger.debug("update ticket - super user type: " + sessMan.getSuperUser());

      if (((numTickets + sessMan.getTicketCount()) <= currEvent.getMaxRegistrants()) ||
          (numTickets < sessMan.getUserTicketCount()) ||
          sessMan.getSuperUser()) {
          sessMan.updateTicketById(ticketId,
                                   numTickets,
                                   comment,
                                   type);
          sessMan.setMessage("Ticket number " + ticketId + " updated.");
      } else {
        logger.info("registration is full");
        sessMan.setMessage("Registration is full: you may only decrease or remove tickets.");
      }

      if (sessMan.getSuperUser()) {
        return new ModelAndView("forward:suagenda.do");
      } else {
        return new ModelAndView("forward:agenda.do");
      }
    } catch (Exception e) {
      logger.error(this, e);
      throw e;
    } catch (Throwable t) {
      logger.error(this, t);
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
