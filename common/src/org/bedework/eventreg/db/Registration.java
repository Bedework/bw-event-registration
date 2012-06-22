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



/**
 * @author douglm
 *
 */
public class Registration extends DbItem<Registration> {
  private String ticketid;
  private String authid;
  private String email;
  private String eventHref;
  private int numTickets;
  private int ticketsRequested;
  private String type;
  private String created;
  private String comment;

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
  public void setTicketid(final String val) {
    ticketid = val;
  }

  /**
   * @return ticketid
   */
  public String getTicketid() {
    return ticketid;
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
  public void setEventHref(final String val) {
    eventHref = val;
  }

  /**
   * @return eventHref
   */
  public String getEventHref() {
    return eventHref;
  }

  /**
   * @param val - tickets allocated
   */
  public void setNumTickets(final int val) {
    numTickets = val;
  }

  /**
   * @return numTickets
   */
  public int getNumTickets() {
    return numTickets;
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
  public void setComment(final String val) {
    comment = val;
  }

  /**
   * @return comment
   */
  public String getComment() {
    return comment;
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
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final Registration that) {
    return getTicketid().compareTo(that.getTicketid());
  }

  @Override
  public int hashCode() {
    return getTicketid().hashCode();
  }
}


