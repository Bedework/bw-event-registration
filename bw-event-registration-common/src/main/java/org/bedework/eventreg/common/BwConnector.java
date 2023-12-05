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
package org.bedework.eventreg.common;

import org.bedework.eventreg.db.Event;
import org.bedework.util.calendar.XcalUtil.TzGetter;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import ietf.params.xml.ns.icalendar_2.ArrayOfComponents;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalWsService;
import org.oasis_open.docs.ws_calendar.ns.soap.CalWsServicePortType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.ObjectFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/** Implements the client end of a SOAP connection for a single eventreg session.
 *
 */
public class BwConnector implements Logged {
  private final TzGetter tzs;

  private final String wsdlUri;

  private final Map<String, Event> events = new HashMap<>();

  protected ObjectFactory of = new ObjectFactory();
  protected JAXBContext jc;

  /**
   * @param wsdlUri for SOAP
   * @param tzs getter for timezones
   */
  public BwConnector(final String wsdlUri,
                     final TzGetter tzs) {
    this.wsdlUri = wsdlUri;
    this.tzs = tzs;
  }

  /** Flush any trace of events.
   *
   */
  public void flush() {
    events.clear();
  }

  /**
   * @param href of event
   * @return cached event or event retrieved from service
   */
  public Event getEvent(final String href) {
    Event ev = events.get(href);

    if (ev != null) {
      return ev;
    }

    /* XXX If the href is terminated with an anchor - e.g. #2012...
     * we're referring to an instance rather than the entire event. We have to
     * pull the whole event so we should cache that. We need to create a cached
     * instance as well.
     *
     * That calls for 2 caches. The returned icalendar and the converted event info.
     */

    // Pull from server.

    final FetchItemResponseType fir = fetchItem(href);

    if ((fir == null) ||
        (fir.getIcalendar() == null) ||
        (fir.getIcalendar().getVcalendar().size() != 1) ||
        (fir.getIcalendar().getVcalendar().get(0).getComponents() == null)) {
      return null;
    }

    final ArrayOfComponents aoc =
            fir.getIcalendar().getVcalendar().get(0).getComponents();

    final List<BaseComponentType> comps = new ArrayList<>();

    for (final JAXBElement<? extends BaseComponentType> comp:
            aoc.getBaseComponent()) {
      comps.add(comp.getValue());
    }

    if (comps.size() != 1) {
      // XXX Fix this stuff
      return null;
    }

    ev = new Event(comps.get(0), href, tzs);

    events.put(href, ev);

    return ev;
  }

  /**
   * @param href of item to fetch
   * @return FetchItemResponseType
   */
  public FetchItemResponseType fetchItem(final String href) {
    //ObjectFactory of = getIcalObjectFactory();

    final FetchItemType fi = new FetchItemType();

    fi.setHref(href);

    return getPort().fetchItem(fi);
  }

  protected CalWsServicePortType getPort() {
    return getPort(wsdlUri);
  }

  protected CalWsServicePortType getPort(final String uri) {
    final URL wsURL;
    try {
      wsURL = new URL(uri);
    } catch (final MalformedURLException e) {
      throw new RuntimeException(e);
    }

    final CalWsService svc =
        new CalWsService(wsURL,
                         new QName("http://docs.oasis-open.org/ws-calendar/ns/soap",
                                   "CalWsService"));
    final CalWsServicePortType port = svc.getCalWsPort();

//    Map requestContext = ((BindingProvider)port).getRequestContext();
//    requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
//    requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);

    return port;
  }

  /* ====================================================================
   *                   Logged methods
   * ==================================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}