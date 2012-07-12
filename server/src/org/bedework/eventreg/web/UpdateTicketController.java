package org.bedework.eventreg.web;

import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.Registration;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author douglm
 */
public class UpdateTicketController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest(final HttpServletRequest request,
                                final HttpServletResponse response) throws Throwable {
    Long ticketId = sessMan.getTicketId();
    if (ticketId == null) {
      return errorReturn("No ticketid supplied");
    }

    int numTickets = sessMan.getTicketsRequested();
    
    logger.debug("updating ticket " + ticketId);
    
    Registration reg = sessMan.getRegistrationById(ticketId);
    
    if (reg == null) {
      sessMan.setMessage("No registration found.");
    } else if (!sessMan.getAdminUser() &&
      !reg.getAuthid().equals(sessMan.getCurrentUser())) {
      sessMan.setMessage("You are not authorized to update that registration.");
    } else {
    
      Event currEvent = sessMan.getCurrEvent();
      String comment = sessMan.getComment();
  
      if ((numTickets + sessMan.getRegTicketCount() - reg.getNumTickets()) <= currEvent.getMaxTickets()) {
        
        sessMan.updateRegistration(reg,numTickets);
        
        sessMan.setMessage("Ticket number " + ticketId + " updated.");
      } else {
        logger.info("Registration is full");
        return errorReturn("Registration is full: you may only decrease or remove tickets.");
      }
    }

    if (sessMan.getAdminUser()) {
      return sessModel("forward:adminagenda.do");
    }

    return sessModel("forward:agenda.do");
  }
}
