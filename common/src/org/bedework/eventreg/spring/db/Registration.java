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

package org.bedework.eventreg.spring.db;


/**
 * @author douglm
 *
 */
public class Registration extends DbItem<Registration> {
  private String email;
  private String uid;
  private String queryStr;
  private String eventid;
  private int numTickets;
  private String type;
  private String created;
  private String comment;

  /**
   *
   */
  public Registration() {

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
  public void setUid(final String val) {
    uid = val;
  }

  /**
   * @return uid
   */
  public String getUid() {
    return uid;
  }

  /**
   * @param val
   */
  public void setQueryStr(final String val) {
    queryStr = val;
  }

  /**
   * @return queryStr
   */
  public String getQueryStr() {
    return queryStr;
  }

  /**
   * @param val
   */
  public void setEventid(final String val) {
    eventid = val;
  }

  /**
   * @return eventid
   */
  public String getEventid() {
    return eventid;
  }

  /**
   * @param val
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
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final Registration that) {
    int i = getEmail().compareTo(that.getEmail());

    if (i != 0) {
      return i;
    }

    return getUid().compareTo(that.getUid());
  }

  @Override
  public int hashCode() {
    return getEmail().hashCode() * getUid().hashCode();
  }
}


