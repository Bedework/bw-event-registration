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

/** Concrete representation of a ticket. Will allow validation of tickets
 * presented by attendees.
 *
 * @author douglm
 */
public class Ticket extends DbItem<Ticket> {
  private Long registrationId;
  private String authid;
  private String href;
  private String uuid;
  private String created;

  /* Non db fields */

  private Event event;

  /**
   *
   */
  public Ticket() {
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
  public void setHref(final String val) {
    href = val;
  }

  /**
   * @return eventHref
   */
  public String getHref() {
    return href;
  }

  /** Unique id for ticket verification
   *
   * @param val
   */
  public void setUuid(final String val) {
    uuid = val;
  }

  /**
   * @return uuid
   */
  public String getUuid() {
    return uuid;
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
    ts.append("href", getHref());
    ts.append("uuid", getUuid());
    ts.append("created", getCreated());
  }

  /* ====================================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final Ticket that) {
    return getUuid().compareTo(that.getUuid());
  }

  @Override
  public int hashCode() {
    return getUuid().hashCode();
  }

  @Override
  public String toString() {
    ToString ts = new ToString(this);

    toStringSegment(ts);

    return ts.toString();
  }
}


