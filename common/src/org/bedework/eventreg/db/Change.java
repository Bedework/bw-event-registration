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
 * Change log entry
 *
 */
public class Change extends DbItem<Change> {
  private Long ticketid;
  private String authid;
  private String lastmod;
  private String type;
  private String name;
  private String value;

  /** */
  public static final String typeNewReg = "New_Reg";

  /** */
  public static final String typeUpdReg = "Upd_Reg";

  /** */
  public static final String lblUpdNumTickets = "numTickets";

  /* Non db fields */

  private Registration registration;

  /**
   *
   */
  public Change() {
  }

  /**
   * @param val
   */
  public void setTicketid(final Long val) {
    ticketid = val;
  }

  /**
   * @return ticketid
   */
  public Long getTicketid() {
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
  public void setType(final String val) {
    type = val;
  }

  /**
   * @return type of change
   */
  public String getType() {
    return type;
  }

  /**
   * @param val
   */
  public void setName(final String val) {
    name = val;
  }

  /**
   * @return name of changed value
   */
  public String getName() {
    return name;
  }

  /**
   * @param val
   */
  public void setValue(final String val) {
    value = val;
  }

  /**
   * @return name of changed value
   */
  public String getValue() {
    return value;
  }

  /* ====================================================================
   *                   Non db fields
   * ==================================================================== */

  /**
   * @param val
   */
  public void setRegistration(final Registration val) {
    registration = val;
  }

  /**
   * @return event
   */
  public Registration getRegistration() {
    return registration;
  }

  /* ====================================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final Change that) {
    return getTicketid().compareTo(that.getTicketid());
  }

  @Override
  public int hashCode() {
    return getTicketid().hashCode();
  }
}


