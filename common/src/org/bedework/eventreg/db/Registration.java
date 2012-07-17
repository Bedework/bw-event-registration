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
package org.bedework.eventreg.db;

import edu.rpi.sss.util.ToString;

import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author douglm
 *
 */
public class Registration extends DbItem<Registration> {
  private Long registrationId;
  private String authid;
  private String email;
  private String href;
  private int ticketsRequested;
  private String type;
  private String created;
  private String lastmod;
  private String waitqDate;
  private String comment;
  private String message;
  private Set<Ticket> tickets;

  /** Type values
   */
  public static final String typeRegistered = "reg";
  public static final String typeHold = "hold";

  /* Non db fields */

  private Event event;

  /**
   *
   */
  public Registration() {

  }

  /**
   * @param val
   */
  public void setRegistrationId(final Long val) {
    registrationId = val;
  }

  /**
   * @return registrationId
   */
  public Long getRegistrationId() {
    return registrationId;
  }

  /**
   * @param val
   */
  public void setAuthid(final String val) {
    authid = val;
  }

  /**
   * @return authid
   */
  public String getAuthid() {
    return authid;
  }

  /**
   * @param val
   */
  public void setEmail(final String val) {
    email = val;
  }

  /**
   * @return email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param val
   */
  public void setHref(final String val) {
    href = val;
  }

  /**
   * @return eventHref
   */
  public String getHref() {
    return href;
  }

  /**
   * @param val - ticketsRequested
   */
  public void setTicketsRequested(final int val) {
    ticketsRequested = val;
  }

  /**
   * @return ticketsRequested
   */
  public int getTicketsRequested() {
    return ticketsRequested;
  }

  /**
   * @param val
   */
  public void setType(final String val) {
    type = val;
  }

  /**
   * @return type
   */
  public String getType() {
    return type;
  }

  /**
   * @param val
   */
  public void setCreated(final String val) {
    created = val;
  }

  /**
   * @return created
   */
  public String getCreated() {
    return created;
  }

  /**
   * @param val
   */
  public void setLastmod(final String val) {
    lastmod = val;
  }

  /**
   * @return lastmod
   */
  public String getLastmod() {
    return lastmod;
  }

  /**
   * @param val
   */
  public void setWaitqDate(final String val) {
    waitqDate = val;
  }

  /**
   * @return waitqDate
   */
  public String getWaitqDate() {
    return waitqDate;
  }

  /**
   * @param val
   */
  public void setComment(final String val) {
    comment = val;
  }

  /**
   * @return comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * @param val
   */
  public void setMessage(final String val) {
    message = val;
  }

  /**
   * @return message
   */
  public String getMessage() {
    return message;
  }

  public void setTickets(final Set<Ticket> val) {
    tickets = val;
  }

  public Set<Ticket> getTickets() {
    return tickets;
  }

  /* ====================================================================
   *                   Non db fields
   * ==================================================================== */

  /**
   * @param val
   */
  public void setEvent(final Event val) {
    event = val;
  }

  /**
   * @return event
   */
  public Event getEvent() {
    return event;
  }

  /* ====================================================================
   *                   Convenience methods
   * ==================================================================== */

  /** Add our stuff to the StringBuilder
   *
   * @param sb    StringBuilder for result
   */
  @Override
  protected void toStringSegment(final ToString ts) {
    super.toStringSegment(ts);
    ts.append("registrationId", getRegistrationId());
    ts.append("authid", getAuthid());
    ts.append("email", getEmail());
    ts.append("href", getHref());
    ts.append("ticketsRequested", getTicketsRequested());
    ts.append("type", getType());
    ts.append("created", getCreated());
    ts.append("lastmod", getLastmod());
    ts.append("waitqDate", getWaitqDate());
    ts.append("comment", getComment());
    ts.append("message", getMessage());
    ts.append("numTickets", getNumTickets());
  }

  public void addTicket(final Ticket val) {
    Set<Ticket> ts = getTickets();

    if (ts == null) {
      ts = new TreeSet<Ticket>();
      setTickets(ts);
    }

    ts.add(val);
  }

  public void addTicket() {
    Ticket t = new Ticket();

    t.setRegistrationId(getRegistrationId());
    t.setUuid(UUID.randomUUID().toString());
    t.setHref(getHref());
    t.setCreated(new Timestamp(new java.util.Date().getTime()).toString());

    addTicket(t);
  }

  public void addTickets(final int numTickets) {
    for (int i = 0; i < numTickets; i++) {
      addTicket();
    }
  }

  public void removeTicket(final Ticket val) {
    Set<Ticket> ts = getTickets();

    if (ts == null) {
      return;
    }

    ts.remove(val);
  }

  public void removeTickets(final int numTickets) {
    if (getTickets() == null) {
      return;
    }

    for (int i = 0; i < numTickets; i++) {
      getTickets().remove(0);
    }
  }

  /**
   * @return numTickets
   */
  public int getNumTickets() {
    Set<Ticket> ts = getTickets();

    if (ts == null) {
      return 0;
    }

    return ts.size();
  }

  /* ====================================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final Registration that) {
    return getRegistrationId().compareTo(that.getRegistrationId());
  }

  @Override
  public int hashCode() {
    return getRegistrationId().hashCode();
  }

  @Override
  public String toString() {
    ToString ts = new ToString(this);

    toStringSegment(ts);

    return ts.toString();
  }
}

