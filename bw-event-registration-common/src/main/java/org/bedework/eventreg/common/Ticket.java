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

/** Concrete representation of a ticket. Will allow validation of tickets
 * presented by attendees.
 *
 * @author douglm
 */
public interface Ticket extends Comparable<Ticket> {
  /**
   * @param val ticket id
   */
  void setRegistrationId(Long val);

  /**
   * @return registrationId
   */
  Long getRegistrationId();

  /**
   * @param val authid
   */
  void setAuthid(String val);

  /**
   * @return authid
   */
  String getAuthid();

  /**
   * @param val eventHref
   */
  void setHref(String val);

  /**
   * @return eventHref
   */
  String getHref();

  /** Unique id for ticket verification
   *
   * @param val uuid
   */
  void setUuid(String val);

  /**
   * @return uuid
   */
  String getUuid();

  /**
   * @param val created
   */
  void setCreated(String val);

  /**
   * @return created
   */
  String getCreated();

  /**
   * @param val event
   */
  void setEvent(Event val);

  /**
   * @return event
   */
  Event getEvent();
}


