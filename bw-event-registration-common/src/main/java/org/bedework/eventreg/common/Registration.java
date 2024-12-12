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

import org.bedework.eventreg.db.Ticket;

import java.util.Map;
import java.util.Set;

/** A registration to an event for a single person (or entity)
 *
 * @author douglm
 *
 */
public interface Registration {
  /** Type values
   */
  String typeRegistered = "reg";

  /** */
  String typeHold = "hold";

  /**
   * @param val the id
   */
  void setRegistrationId(Long val);

  /**
   * @return registrationId
   */
  Long getRegistrationId();

  /**
   * @param val the authid
   */
  void setAuthid(String val);

  /**
   * @return authid
   */
  String getAuthid();

  /**
   * @param val email of user (do we need this if notify engine has it)
   */
  void setEmail(String val);

  /**
   * @return email
   */
  String getEmail();

  /**
   * @param val href of event
   */
  void setHref(String val);

  /**
   * @return eventHref
   */
  String getHref();

  /**
   * @param val - ticketsRequested
   */
  void setTicketsRequested(int val);

  /**
   * @return ticketsRequested
   */
  int getTicketsRequested();

  /**
   * @param val hold/reg etc
   */
  void setType(String val);

  /**
   * @return type
   */
  String getType();

  /**
   * @param val date
   */
  void setCreated(String val);

  /**
   * @return created
   */
  String getCreated();

  /**
   * @param val date
   */
  void setLastmod(String val);

  /**
   * @return lastmod
   */
  String getLastmod();

  /** The waitqDate is almost the lastmod and orders the waitq. We need a separate
   * date to avoid somebody going to the back of the queue because of a trivial
   * modification. For example changing the number of tickets required should
   * preserve your place if you're already on the q.
   *
   * <p>On the other hand we need to ensure that somebody goes to the back of the
   * queue if they are already fulfilled but decide they want more.
   *
   * @param val date
   */
  void setWaitqDate(String val);

  /**
   * @return waitqDate
   */
  String getWaitqDate();

  /**
   * @param val a comment
   */
  void setComment(String val);

  /**
   * @return comment
   */
  String getComment();

  /**
   * @param val a message
   */
  void setMessage(String val);

  /**
   * @return message
   */
  String getMessage();

  /**
   * @param val name
   */
  void setFormName(String val);

  /**
   * @return name
   */
  String getFormName();

  /**
   * @param val name
   */
  void setFormOwner(String val);

  /**
   * @return name
   */
  String getFormOwner();

  /**
   * @param val set of tickets
   */
  void setTickets(Set<Ticket> val);

  /**
   * @return tickets
   */
  Set<Ticket> getTickets();

  String getEvSummary();

  void setEvSummary(String evSummary);

  String getEvDate();

  void setEvDate(String evDate);

  String getEvTime();

  void setEvTime(String evTime);

  String getEvLocation();

  void setEvLocation(String evLocation);

  String getNotificationTimestamp();

  void setNotificationTimestamp(String notificationTimestamp);

  /**
   * @param val flag to say we sent a cancel message
   */
  void setCancelSent(boolean val);

  /**
   * @return flag to say we sent a cancel message
   */
  boolean getCancelSent() ;

  /**
   * @param val Saved form fields as a serialized json string
   */
  void setFormValues(String val);

  /**
   * @return Saved form fields as a serialized json string
   */
  String getFormValues();

  /* ==========================================================
   *                   Non db fields
   * ========================================================== */

  /**
   * @param val the event
   */
  void setEvent(Event val);

  /**
   * @return event
   */
  Event getEvent();

  void saveFormValues(Map<String, ?> vals);

  Map<?, ?> restoreFormValues();

  /** Set the various timestamps to now for a new registration.
   *
   */
  void setTimestamps();

  /** Set the lastmod to now.
   *
   */
  void setLastmod();

  /** Set the waitq date to now.
   *
   */
  void setWaitqDate();

  /**
   * @param val a ticket
   */
  void addTicket(Ticket val);

  /** Add a new ticket
   *
   */
  void addTicket();

  /**
   * @param numTickets to add
   */
  void addTickets(int numTickets);

  /**
   * @param val Ticket
   */
  void removeTicket(Ticket val);

  /**
   * @param numTickets to remove
   */
  void removeTickets(int numTickets);

  /**
   * @return numTickets
   */
  int getNumTickets();
}

