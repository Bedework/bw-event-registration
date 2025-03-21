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
package org.bedework.eventreg.service;

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.requests.EventChangeRequest;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.eventreg.requests.RegistrationAction;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.http.Headers;
import org.bedework.util.http.PooledHttpClient;
import org.bedework.util.jms.JmsNotificationsHandlerImpl;
import org.bedework.util.jms.NotificationException;
import org.bedework.util.jms.NotificationsHandler;
import org.bedework.util.jms.events.SysEvent;
import org.bedework.util.jms.listeners.JmsSysEventListener;
import org.bedework.util.misc.AbstractProcessorThread;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleIcalTags;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.CaldavDefs;
import org.bedework.util.xml.tagdefs.WebdavTags;

import org.apache.http.HttpException;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bedework.util.misc.AbstractProcessorThread.stopProcess;
import static org.bedework.util.xml.tagdefs.WebdavTags.href;
import static org.bedework.util.xml.tagdefs.WebdavTags.namespace;
import static org.bedework.util.xml.tagdefs.WebdavTags.principalURL;

/** Does the work of processing a request
 *
 * @author douglm
 *
 */
public class SvcRequestHandler extends JmsSysEventListener
        implements EventregRequestHandler {
  private final EventregDb db;
  private final EventregProperties props;

  private boolean open;

  protected StringWriter davXmlSw;
  protected XmlEmit davXml;

  protected PooledHttpClient client;

  private final URI bwUri;

  private final NotificationsHandler sender;

  private final SvcRequestDelayHandler delayHandler;

  private final static XcalUtil.TzGetter tzs = Timezones::getTz;

  private final BwConnector cnctr;

  private static class Processor extends AbstractProcessorThread {
    private final SvcRequestHandler handler;

    /**
     * @param name for the thread
     */
    public Processor(final String name,
                     final SvcRequestHandler handler) {
      super(name);
      this.handler = handler;
    }

    @Override
    public void runInit() {
    }

    @Override
    public void runProcess() {
      handler.listen();
    }

    @Override
    public void end(final String msg) {
    }

    @Override
    public void close() {
      handler.close();
    }
  }

  private AbstractProcessorThread processor;

  public SvcRequestHandler(final EventregProperties props) {
    this.props = props;

    db = new EventregDb();
    db.setSysInfo(getSysInfo());

    delayHandler = new SvcRequestDelayHandler(this, props);

    try {
      Timezones.initTimezones(getSysInfo().getTzsUri());

      cnctr = new BwConnector(getSysInfo().getWsdlUri(), tzs);

      sender = new JmsNotificationsHandlerImpl(
              props.getActionQueueName(),
              ConfigBase.toProperties(
                      props.getSyseventsProperties()));

      bwUri = new URI(getSysInfo().getBwUrl());
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  @Override
  public void action(final SysEvent ev) {
    if (ev == null) {
      return;
    }

    if (debug()) {
      debug("handling request: " + ev);
    }

    if (!(ev instanceof final EventregRequest req)) {
      return;
    }

    boolean ok = false;

    try {
      ok = handle(req);
    } catch (final Throwable t) {
      error("Error handling request: " + req);
      error(t);
    }

    if (ok) {
      if (debug()) {
        debug("Success processing message.");
      }
      return;
    }

    if (debug()) {
      debug("Failed to process message. Adding to delay handler queue");
    }
    delayHandler.delay(req);
  }

  public void listen() {
    try {
      open(props.getActionQueueName(),
           ConfigBase.toProperties(props.getSyseventsProperties()));

      process(false);
    } catch (final Throwable t) {
      error(t);
      throw new EventregException(t);
    }
  }

  @Override
  public void addRequest(final EventregRequest val) {
    try {
      sender.post(val);
    } catch (final NotificationException e) {
      throw new EventregException(e);
    }
  }

  public void close() {
    //stop();
    super.close();
  }

  AbstractProcessorThread getProcessor() {
    return new Processor("EventregAction", this);
  }

  public boolean isRunning() {
    if (processor == null) {
      return false;
    }

    if (!processor.isAlive()) {
      processor = null;
      return false;
    }

    if (processor.getRunning()) {
      return true;
    }

    stopProcess(processor);
    if (!processor.isAlive()) {
      processor = null;
      return false;
    }

    warn("Processor was unstoppable. Acquiring new processor");
    processor = null;
    return false;
  }

  public synchronized void start() {
    if (!delayHandler.isRunning()) {
      delayHandler.start();
    }

    if (isRunning()) {
      error("Already started");
      return;
    }

    try {
      processor = getProcessor();
    } catch (final Throwable t) {
      error("Error getting processor");
      error(t);
      return;
    }
    processor.setRunning(true);
    processor.start();
  }

  public synchronized void stop() {
    info("Stop called for " + getClass().getSimpleName());
    if (delayHandler.isRunning()) {
      delayHandler.stop();
    }

    if (processor == null) {
      error("Already stopped");
      return;
    }

    stopProcess(processor);
    processor = null;
  }

  private boolean handle(final EventregRequest request) {
    try {
      openDb();

      if (request instanceof EventChangeRequest) {
        return handleChange((EventChangeRequest)request);
      }

      if (request instanceof RegistrationAction) {
        return handleNewReg((RegistrationAction)request);
      }

      request.discard();
      return false;
    } finally {
      closeDb();
    }
  }

  private boolean handleChange(final EventChangeRequest request) {
      /* We need to fetch the event, possibly adjust tickets and
         maybe notify people.
       */
    final String href = request.getHref();
    cnctr.flush();
    final Event ev = cnctr.getEvent(href);

    if (ev == null) {
      warn("No event found for " + href);
      return true; // effectively ignore
    }

    final String status = ev.getStatus();

    if (debug()) {
      debug("change: status=" + status + " for event " + ev.getSummary());
    }

    /* For the time being we'll only handle events that are canceled */
    if (!IcalDefs.statusCancelled.equalsIgnoreCase(status)) {
      return true; // Nothing to do
    }

    // Have we seen this already?
    final var regs = db.getByEvent(href);

    if (Util.isEmpty(regs)) {
      // No registrations
      return true;
    }

    final List<String> principals = new ArrayList<>();
    final List<Registration> update = new ArrayList<>();

    for (final var reg: regs) {
      if (reg.getCancelSent()) {
        continue;
      }

      principals.add(reg.getAuthid());
      update.add(reg);
    }

    /* Tell the calendar system we need to notify the user. We send
         something like:
          <?xml version="1.0" encoding="UTF-8" ?>

          <BSS:eventregCancelled xmlns:C="urn:ietf:params:xml:ns:caldav"
                        xmlns:BSS="http://bedework.org/ns/"
                        xmlns:BW="http://bedeworkcalserver.org/ns/"
                        xmlns:CSS="http://calendarserver.org/ns/"
                        xmlns:DAV="DAV:">
              <DAV:href>/ucaldav/public/cals/MainCal/123456.ics</DAV:href>
              <CSS:uid>92565234-0c5d-448f-a775-7ff9f8605df8</CSS:uid>
              <DAV:principal-URL>
                <DAV:href>/ucaldav/principals/users/douglm</DAV:href>
              </DAV:principal-URL>
              <DAV:principal-URL>
                <DAV:href>/ucaldav/principals/users/johnsa</DAV:href>
              </DAV:principal-URL>
              <BSS:comment></BSS:comment>
            </BSS:eventregCancelled>

       */
    final XmlEmit xml = startDavEmit();

    xml.openTag(BedeworkServerTags.eventregCancelled);
    xml.property(WebdavTags.href, href);
    xml.property(AppleServerTags.uid, UUID.randomUUID().toString());

    for (final String pr: principals) {
      doXmlPrUrl(xml, pr);
    }

    xml.closeTag(BedeworkServerTags.eventregCancelled);

    return postXml(endDavEmit());
  }

  private boolean handleNewReg(final RegistrationAction nr) {
      /* We need to fetch the event and notify the registered individual.
       */
    final var reg = nr.getReg();
    final String href = reg.getHref();
    final Event ev = cnctr.getEvent(href);

    if (reg.getEmail() != null) {
      subscribeNotifications(reg);
    }

    /* Tell the calendar system we need to notify the user. We send
         something like:
          <?xml version="1.0" encoding="UTF-8" ?>

          <BSS:eventRegistered xmlns:C="urn:ietf:params:xml:ns:caldav"
                        xmlns:BSS="http://bedework.org/ns/"
                        xmlns:BW="http://bedeworkcalserver.org/ns/"
                        xmlns:CSS="http://calendarserver.org/ns/"
                        xmlns:DAV="DAV:">
            <DAV:href>/ucaldav/public/cals/MainCal/123456.ics</DAV:href>
            <CSS:uid>92565234-0c5d-448f-a775-7ff9f8605df8</CSS:uid>
            <BSS:eventregNumTickets>3</BSS:eventregNumTickets>
            <DAV:principal-URL>
              <DAV:href>/ucaldav/principals/users/douglm</DAV:href>
            </DAV:principal-URL>
            <BSS:comment></BSS:comment>
          </BSS:eventregRegistered>

       */
    final XmlEmit xml = startDavEmit();

    xml.openTag(BedeworkServerTags.eventregRegistered);
    xml.property(WebdavTags.href, href);
    xml.property(AppleServerTags.uid, UUID.randomUUID().toString());
    xml.property(BedeworkServerTags.eventregNumTicketsRequested,
                 String.valueOf(reg.getTicketsRequested()));
    xml.property(BedeworkServerTags.eventregNumTickets,
                 String.valueOf(reg.getNumTickets()));

    doXmlPrUrl(xml, reg.getAuthid());

    xml.closeTag(BedeworkServerTags.eventregRegistered);

    return postXml(endDavEmit());
  }

  private void doXmlPrUrl(final XmlEmit xml,
                          final String pr) {
    xml.openTag(principalURL);

    if (pr.startsWith("/")) {
      xml.property(href, pr);
    } else if (pr.startsWith("mailto:")) {
      xml.property(href, pr);
    } else {
      xml.property(href, "/principals/users/" + pr);
    }
    xml.closeTag(principalURL);
  }

  public EventregProperties getSysInfo() {
    return props;
  }

  /**
   */
  public synchronized void openDb() {
    if (db == null) {
      return;
    }

    if (open) {
      //???
      return;
    }
    db.open();
    open = true;
  }

  /**
   * @return false if something failed on close - probably a commit error
   */
  public synchronized boolean closeDb() {
    if (db == null) {
      return true;
    }

    db.close();
    return true;
  }

  protected boolean subscribeNotifications(final Registration reg) {
    final String email = reg.getEmail();

    if (email == null) {
      error("No email");
      return false;
    }

    /* Tell the calendar system we need to notify the user. We send
         something like:
          <?xml version="1.0" encoding="UTF-8" ?>

          <BSS:notifySubscribe xmlns:C="urn:ietf:params:xml:ns:caldav"
                        xmlns:BSS="http://bedework.org/ns/"
                        xmlns:BW="http://bedeworkcalserver.org/ns/"
                        xmlns:CSS="http://calendarserver.org/ns/"
                        xmlns:DAV="DAV:">
            <DAV:principal-URL>
              <DAV:href>/ucaldav/principals/users/douglm</DAV:href>
            </DAV:principal-URL>
            <BSS:action>add</BSS:action>
            <BSS:email>fred@example.org</BSS:email>
          </BSS:notifySubscribe>

       */
    final XmlEmit xml = startDavEmit();

    xml.openTag(BedeworkServerTags.notifySubscribe);

    xml.openTag(principalURL);
    xml.property(href, makePrincipal(reg.getAuthid()));
    xml.closeTag(principalURL);

    xml.property(BedeworkServerTags.action, "add");
    xml.property(BedeworkServerTags.email, email);

    xml.closeTag(BedeworkServerTags.notifySubscribe);

    return postXml(endDavEmit());
  }

  protected boolean postXml(final String xml) {
    try {
      final var resp = getClient().postXml("", xml);

      return !resp.failed;
    } catch (final HttpException he) {
      throw new EventregException(he);
    }
  }

  protected String makePrincipal(final String id) {
    if (id.startsWith("/")) {
      return id;
    }

    if (id.startsWith("mailto:")) {
      return id;
    }

    return "/principals/users/" + id;
  }

  protected PooledHttpClient getClient() {
    if (client != null) {
      return client;
    }

    try {
      client = new PooledHttpClient(bwUri);

      client.setHeadersFetcher(this::getAuthHeaders);

      return client;
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  Headers getAuthHeaders() {
    final String id = getSysInfo().getBwId();
    final String token = getSysInfo().getBwToken();

    if ((id == null) || (token == null)) {
      return null;
    }

    return new Headers()
            .add("X-BEDEWORK-NOTE", id + ":" + token)
            .add("X-BEDEWORK-EXTENSIONS", "true");
  }

  protected XmlEmit startDavEmit() {
    try {
      davXmlSw = new StringWriter();
      davXml = new XmlEmit();
      davXml.startEmit(davXmlSw);

      addNamespace(davXml);

      return davXml;
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  protected String endDavEmit() {
    try {
      davXml.flush();
      return davXmlSw.toString();
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  public void addNamespace(final XmlEmit xml) {
    try {
      xml.addNs(new XmlEmit.NameSpace(namespace, "DAV"), true);

      xml.addNs(new XmlEmit.NameSpace(CaldavDefs.caldavNamespace, "C"), true);
      xml.addNs(new XmlEmit.NameSpace(AppleIcalTags.appleIcalNamespace, "AI"), false);
      xml.addNs(new XmlEmit.NameSpace(CaldavDefs.icalNamespace, "ical"), false);
      xml.addNs(new XmlEmit.NameSpace(AppleServerTags.appleCaldavNamespace, "CS"), false);
      xml.addNs(new XmlEmit.NameSpace(BedeworkServerTags.bedeworkCaldavNamespace, "BSS"), false);
      xml.addNs(new XmlEmit.NameSpace(
                        BedeworkServerTags.bedeworkSystemNamespace,
                        "BW"),
                false);
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }
}
