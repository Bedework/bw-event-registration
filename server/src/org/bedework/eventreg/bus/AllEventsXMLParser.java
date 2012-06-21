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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.ArrayList;

public class AllEventsXMLParser extends DefaultHandler {
  protected final Log logger = LogFactory.getLog(getClass());
  private boolean inStart = false;

  Event currEvent;
  ArrayList<Event> allEvents = new ArrayList<Event>();

  private CharArrayWriter contents = new CharArrayWriter();

  public AllEventsXMLParser() {
    logger.info("Starting XML Parser");
  }

  @Override
  public void startElement(final String namespaceURI,
                           final String localName,
                           final String qName,
                           final Attributes attr) throws SAXException {
    contents.reset();

    if (localName.equals("event")) {
      logger.info("Event Parser - found properties");
      currEvent = new Event();
    }

    if (localName.equals( "start")) {
      logger.info("Event start date Parser - found properties");
      inStart = true;
    }
  }

  @Override
  public void endElement(final String namespaceURI,
                         final String localName,
                         final String qName) throws SAXException {

    String content = "";

    if (localName.equals("guid")) {
      content = contents.toString();
      currEvent.setUid(content.trim());
    }

    if (localName.equals("summary")) {
      content = contents.toString();
      currEvent.setSummary(content.trim());
    }

    if (inStart) {
      if (localName.equals("longdate")) {
        content = contents.toString();
        currEvent.setDate(content.trim());
      }

      if (localName.equals("time")) {
        content = contents.toString();
        currEvent.setTime(content.trim());
      }

      if (localName.equals("utcdate")) {
        content = contents.toString();
        currEvent.setUtc(content.trim());
      }
    }

    if (localName.equals("address")) {
      content = contents.toString();
      currEvent.setLocation(content.trim());
    }

    if (localName.equals("X-BEDEWORK-EVENT-TYPE")) {
      content = contents.toString();
      currEvent.setEventType(content.trim());
    }

    if (localName.equals("X-BEDEWORK-MAX-REGISTRANTS"))  {
      content = contents.toString();
      currEvent.setMaxRegistrants(new Integer(content.trim()).intValue());
    }

    if (localName.equals("X-BEDEWORK-REGISTRATION-DEADLINE")) {
      content = contents.toString();
      currEvent.setRegDeadline(content.trim());
    }

    if (localName.equals("X-BEDEWORK-TICKETS-ALLOWED")) {
      content = contents.toString();
      currEvent.setTicketsAllowed(new Integer(content.trim()).intValue());
    }

    // reset sections when out of a section block
    if (localName.equals("start")) {
      inStart = false;
    }

    if (localName.equals("event") ) {
      allEvents.add(currEvent);
    }

  }

  @Override
  public void characters(final char[] ch,
                         final int start,
                         final int length) throws SAXException {
    contents.write( ch, start, length );
  }

  public ArrayList<Event> getEvents()  {
    return allEvents;
  }

  public String Parse(final String xmlDoc) {
    try {
      // Create SAX 2 parser...
      XMLReader xr = XMLReaderFactory.createXMLReader();

      // Set the ContentHandler...

      xr.setContentHandler(this);

      // Parse the file...
      xr.parse(new InputSource(new StringReader( xmlDoc )));
    } catch (Exception e)  {
      e.printStackTrace();
      logger.info(e);
    }

    return "";
  }
}
