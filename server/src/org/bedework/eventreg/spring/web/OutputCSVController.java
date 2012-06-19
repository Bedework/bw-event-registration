package org.bedework.eventreg.spring.web;

import org.bedework.eventreg.spring.bus.AllEventsXMLParser;
import org.bedework.eventreg.spring.bus.Event;
import org.bedework.eventreg.spring.bus.SessionManager;
import org.bedework.eventreg.spring.db.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class OutputCSVController implements Controller {
  /**   */
  public final static String EVENTINFOURL =
      "http://events.rpi.edu/main/listEvents.do" +
         "?start=2009-12-04" +
          "&end=2009-12-07" +
          "&setappvar=creator(agrp_EMPACSpecial)" +
          "&skinName=empacreg";

  protected final Log logger = LogFactory.getLog(getClass());

  private SessionManager sessMan;

  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
    logger.debug("OutputCSVController entry");

    if (!sessMan.getSuperUser()) {
      logger.info("Non superuser attempted to access OutputCSVController.");
      return new ModelAndView("error");
    }

    String urltext;

    try {
      urltext = sessMan.getURL(EVENTINFOURL);

      AllEventsXMLParser aep = new AllEventsXMLParser();
      aep.Parse(urltext);

      HashMap<String, Event> eventInfoHash = new HashMap<String, Event>();

      for (Event currEvent: aep.getEvents()) {
        eventInfoHash.put(currEvent.getEventGUID(), currEvent);
        logger.info(currEvent.getEventGUID());
      }

      ArrayList allRegistrationInfo = new ArrayList();

      for (Registration reg: sessMan.getAllRegistrations()) {
        HashMap currRegistrationHash = new HashMap();

        String currGUID = reg.getUid();

        currRegistrationHash.put("eventguid",currGUID);

        Event eventInfo = eventInfoHash.get(currGUID);
        currRegistrationHash.put("event", eventInfo.getEventSummary());
        currRegistrationHash.put("date", eventInfo.getEventDateStr());
        currRegistrationHash.put("time", eventInfo.getEventTimeStr());
        currRegistrationHash.put("location", eventInfo.getEventLocation());

  //      currRegistrationHash.put("ticketid", reg.get("id"));
    //    currRegistrationHash.put("empacid", reg.get("eventid"));
        currRegistrationHash.put("email", reg.getEmail());
        currRegistrationHash.put("numtickets", reg.getNumTickets());
        currRegistrationHash.put("type", reg.getType());
        currRegistrationHash.put("comment", reg.getComment());
        currRegistrationHash.put("eventcreated", reg.getCreated());


  // ???      currRegistrationHash.put("rpi", reg.get("rpi"));

        allRegistrationInfo.add(currRegistrationHash);
      }

      Map myModel = new HashMap();
      myModel.put("registrations", allRegistrationInfo);

      return new ModelAndView("csv", myModel);
    } catch (Exception e) {
      logger.error(this, e);
      throw e;
    } catch (Throwable t) {
      logger.error(this, t);
      throw new Exception(t);
    }
  }

  public void setSessionManager(final SessionManager sm) {
    sessMan = sm;
  }
}
