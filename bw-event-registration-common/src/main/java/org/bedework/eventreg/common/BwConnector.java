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

import ietf.params.xml.ns.icalendar_2.ArrayOfComponents;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import org.apache.log4j.Logger;
import org.oasis_open.docs.ws_calendar.ns.soap.CalWsService;
import org.oasis_open.docs.ws_calendar.ns.soap.CalWsServicePortType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.ObjectFactory;
import org.w3c.dom.Document;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

/** Implements the client end of a SOAP connection for a single eventreg session.
 *
 */
public class BwConnector {
  private transient Logger log;

  private final TzGetter tzs;

  private final static ietf.params.xml.ns.icalendar_2.ObjectFactory icalOf =
      new ietf.params.xml.ns.icalendar_2.ObjectFactory();

  private final String wsdlUri;

  private final Map<String, Event> events = new HashMap<>();

  protected ObjectFactory of = new ObjectFactory();
  protected MessageFactory soapMsgFactory;
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
   * @throws Throwable
   */
  public Event getEvent(final String href) throws Throwable {
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
   * @throws Throwable
   */
  public FetchItemResponseType fetchItem(final String href) throws Throwable {
    //ObjectFactory of = getIcalObjectFactory();

    final FetchItemType fi = new FetchItemType();

    fi.setHref(href);

    return getPort().fetchItem(fi);
  }

  protected ietf.params.xml.ns.icalendar_2.ObjectFactory getIcalObjectFactory() {
    return icalOf;
  }

  protected CalWsServicePortType getPort() throws Throwable {
    return getPort(wsdlUri);
  }

  protected CalWsServicePortType getPort(final String uri) throws Throwable {
    URL wsURL = new URL(uri);

    CalWsService svc =
        new CalWsService(wsURL,
                         new QName("http://docs.oasis-open.org/ws-calendar/ns/soap",
                                   "CalWsService"));
    CalWsServicePortType port = svc.getCalWsPort();

//    Map requestContext = ((BindingProvider)port).getRequestContext();
//    requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
//    requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);

    return port;
  }

  @SuppressWarnings("rawtypes")
  protected Object unmarshalBody(final HttpServletRequest req) throws Throwable {
    SOAPMessage msg = getSoapMsgFactory().createMessage(null, // headers
                                                        req.getInputStream());

    SOAPBody body = msg.getSOAPBody();

    Unmarshaller u = getSynchJAXBContext().createUnmarshaller();

    Object o = u.unmarshal(body.getFirstChild());

    if (o instanceof JAXBElement) {
      // Some of them get wrapped.
      o = ((JAXBElement)o).getValue();
    }

    return o;
  }

  protected void marshal(final Object o,
                         final OutputStream out) throws Throwable {
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    Document doc = dbf.newDocumentBuilder().newDocument();

    SOAPMessage msg = soapMsgFactory.createMessage();
    msg.getSOAPBody().addDocument(doc);

    marshaller.marshal(o,
                       msg.getSOAPBody());

    msg.writeTo(out);
  }

  protected MessageFactory getSoapMsgFactory() throws Throwable {
    if (soapMsgFactory == null) {
      soapMsgFactory = MessageFactory.newInstance();
    }

    return soapMsgFactory;
  }

  protected void info(final String msg) {
    getLogger().info(msg);
  }

  protected void trace(final String msg) {
    getLogger().debug(msg);
  }

  protected void error(final Throwable t) {
    getLogger().error(this, t);
  }

  protected void error(final String msg) {
    getLogger().error(msg);
  }

  protected void warn(final String msg) {
    getLogger().warn(msg);
  }

  /* Get a logger for messages
   */
  protected Logger getLogger() {
    if (log == null) {
      log = Logger.getLogger(this.getClass());
    }

    return log;
  }

  /* ====================================================================
   *                         Package methods
   * ==================================================================== */

  JAXBContext getSynchJAXBContext() throws Throwable {
    if (jc == null) {
      jc = JAXBContext.newInstance("org.bedework.synch.wsmessages:" +
          "ietf.params.xml.ns.icalendar_2");
    }

    return jc;
  }
}