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
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.Registration;
import org.bedework.eventreg.requests.EventChangeRequest;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.eventreg.requests.NewRegistration;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.http.BasicHttpClient;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleIcalTags;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.CaldavDefs;
import org.bedework.util.xml.tagdefs.WebdavTags;

import net.fortuna.ical4j.model.TimeZone;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Does the work of processing a request
 *
 * @author douglm
 *
 */
public class SvcRequestHandler implements EventregRequestHandler {
  private final EventregDb db;
  private final EventregProperties props;

  private boolean open;

  protected StringWriter davXmlSw;
  protected XmlEmit davXml;

  protected BasicHttpClient bwClient;

  private final static XcalUtil.TzGetter tzs = new XcalUtil.TzGetter() {
    @Override
    public TimeZone getTz(final String id) throws Throwable {
      return Timezones.getTz(id);
    }
  };

  private final BwConnector cnctr;

  public SvcRequestHandler(final EventregProperties props) throws Throwable {
    this.props = props;

    db = new EventregDb();
    db.setSysInfo(getSysInfo());

    Timezones.initTimezones(getSysInfo().getTzsUri());

    cnctr = new BwConnector(getSysInfo().getWsdlUri(), tzs);
  }

  public void handle(final EventregRequest request) throws Throwable {
    try {
      openDb();

      if (request instanceof EventChangeRequest) {
        handleChange((EventChangeRequest)request);
      }

      if (request instanceof NewRegistration) {
        handleNewReg((NewRegistration)request);
      }
    } finally {
      closeDb();
    }
  }

  private void handleChange(final EventChangeRequest request) throws Throwable {
      /* We need to fetch the event, possibly adjust tickets and
         maybe notify people.
       */
    final String href = request.getHref();
    final Event ev = cnctr.getEvent(href);

    final String status = ev.getStatus();
    if ((status != null) && IcalDefs.statusCancelled.equalsIgnoreCase(status)) {
      // Have we seen this already?
      final List<Registration> regs = db.getByEvent(href);

      if (Util.isEmpty(regs)) {
        // No registrations
        return;
      }

      final List<String> principals = new ArrayList<>();
      final List<Registration> update = new ArrayList<>();

      for (final Registration reg: regs) {
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
        xml.openTag(WebdavTags.principalURL);
        if (pr.startsWith("/")) {
          xml.property(WebdavTags.href, pr);
        } else if (pr.startsWith("mailto:")) {
          xml.property(WebdavTags.href, pr);
        } else {
          xml.property(WebdavTags.href, "/principals/users/" + pr);
        }
        xml.closeTag(WebdavTags.principalURL);
      }

      xml.closeTag(BedeworkServerTags.eventregCancelled);

      final String content = endDavEmit();

      final BasicHttpClient cl = getBwClient();

      final int respStatus = cl.sendRequest("POST",
                                            getSysInfo().getBwUrl(),
                                            getAuthHeaders(),
                                            "application/xml",
                                            content.length(), content.getBytes());
    }
  }

  private void handleNewReg(final NewRegistration nr) throws Throwable {
      /* We need to fetch the event and notify the registered individual.
       */
    final Registration reg = nr.getReg();
    final String href = reg.getHref();
    final Event ev = cnctr.getEvent(href);

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

    xml.openTag(WebdavTags.principalURL);

    final String pr = reg.getAuthid();
    if (pr.startsWith("/")) {
      xml.property(WebdavTags.href, pr);
    } else if (pr.startsWith("mailto:")) {
      xml.property(WebdavTags.href, pr);
    } else {
      xml.property(WebdavTags.href, "/principals/users/" + pr);
    }
    xml.closeTag(WebdavTags.principalURL);

    xml.closeTag(BedeworkServerTags.eventregRegistered);

    final String content = endDavEmit();

    final BasicHttpClient cl = getBwClient();

    final int respStatus = cl.sendRequest("POST",
                                          getSysInfo().getBwUrl(),
                                          getAuthHeaders(),
                                          "application/xml",
                                          content.length(),
                                          content.getBytes());
  }

  public EventregProperties getSysInfo() {
    return props;
  }

  /**
   * @throws Throwable
   */
  public synchronized void openDb() throws Throwable {
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

    open = false;
    return db.close();
  }

  protected BasicHttpClient getBwClient() throws EventregException {
    if (bwClient != null) {
      return bwClient;
    }

    try {
      bwClient = new BasicHttpClient(30 * 1000,
                                     false);  // followRedirects
      bwClient.setBaseURI(new URI(getSysInfo().getBwUrl()));
      //if (sub.getUri() != null) {
      //  client.setBaseURI(new URI(sub.getUri()));
      //}

      return bwClient;
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  private List<Header> authheaders;

  List<Header> getAuthHeaders() throws EventregException {
    if (authheaders != null) {
      return authheaders;
    }

    final String id = getSysInfo().getBwId();
    final String token = getSysInfo().getBwToken();

    if ((id == null) || (token == null)) {
      return null;
    }

    authheaders = new ArrayList<>(1);
    authheaders.add(new BasicHeader("X-BEDEWORK-NOTE", id + ":" + token));
    authheaders.add(new BasicHeader("X-BEDEWORK-EXTENSIONS", "true"));

    return authheaders;
  }

  protected XmlEmit startDavEmit() throws EventregException {
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

  protected String endDavEmit() throws EventregException {
    try {
      davXml.flush();
      return davXmlSw.toString();
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  public void addNamespace(final XmlEmit xml) throws EventregException {
    try {
      xml.addNs(new XmlEmit.NameSpace(WebdavTags.namespace, "DAV"), true);

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
