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
package org.bedework.eventreg.bus;

import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.Registration;
import org.bedework.eventreg.requests.EventChangeRequest;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.eventreg.service.EventregRequestHandler;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.http.BasicHttpClient;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Does the work of processing a request
 *
 * @author douglm
 *
 */
public class SvcRequestHandler implements EventregRequestHandler {
  private final SessionManager sess = new SessionManager();

  public SvcRequestHandler() throws Throwable {
    sess.setEventregDb(new EventregDb());
  }

  public void handle(final EventregRequest request) throws Throwable {
    try {
      sess.openDb();

      if (request instanceof EventChangeRequest) {
        handleChange((EventChangeRequest)request);
      }
    } finally {
      sess.closeDb();
    }
  }

  private void handleChange(final EventChangeRequest request) throws Throwable {
      /* We need to fetch the event, possibly adjust tickets and
         maybe notify people.
       */
    final String href = request.getHref();
    final Event ev = sess.retrieveEvent(href);

    final String status = ev.getStatus();
    if ((status != null) && IcalDefs.statusCancelled.equalsIgnoreCase(status)) {
      // Have we seen this already?
      final List<Registration> regs = sess.getRegistrationsByHref(href);

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
      final XmlEmit xml = sess.startDavEmit();

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

      final String content = sess.endDavEmit();

      final BasicHttpClient cl = sess.getBwClient();

      final int respStatus = cl.sendRequest("POST",
                                            sess.getSysInfo().getBwUrl(),
                                            sess.getAuthHeaders(),
                                            "application/xml",
                                            content.length(), content.getBytes());
    }
  }
}
