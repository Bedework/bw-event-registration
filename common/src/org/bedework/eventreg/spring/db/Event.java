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
public class Event implements Comparable<Event> {
  private String href;
  private String uid;
  private String calPath;
  private String recurrenceId;
  private String eventType;
  private String regDeadline;
  private String date;
  private String utc;
  private String time;
  private String location;
  private String summary;
  private String query;
  private int ticketsAllowed;
  private int totalRegistrants;

  /**
   * @return the href
   */
  public String getHref() {
    return href;
  }

  /**
   * @param val
   */
  public void setHref(final String val) {
    href = val;
  }

  /**
   * @return the uid
   */
  public String getUid() {
    return uid;
  }

  /**
   * @param val
   */
  public void setUid(final String val) {
    uid = val;
  }

  /**
   * @return calendar path for the event
   */
  public String getCalPath() {
    return calPath;
  }

  /**
   * @param val
   */
  public void setCalPath(final String val) {
    calPath = val;
  }

  /**
   * @return recurrence id or null
   */
  public String getRecurrenceId() {
    return recurrenceId;
  }

  /**
   * @param val
   */
  public void setRecurrenceId(final String val) {
    recurrenceId = val;
  }

  /**
   * @return max number of tickets
   */
  public int getTicketsAllowed() {
    return ticketsAllowed;
  }

  /**
   * @param val
   */
  public void setTicketsAllowed(final int val) {
    ticketsAllowed = val;
  }

  /**
   * @return total registrants?
   */
  public int getTotalRegistrants() {
    return totalRegistrants;
  }

  /**
   * @param val
   */
  public void setTotalRegistrants(final int val) {
    totalRegistrants = val;
  }

  /**
   * @return event type ?
   */
  public String getEventType() {
    return eventType;
  }

  /**
   * @param val
   */
  public void setEventType(final String val) {
    eventType = val;
  }

  /**
   * @return the deadline
   */
  public String getRegDeadline() {
    return regDeadline;
  }

  /**
   * @param val
   */
  public void setRegDeadline(final String val) {
    regDeadline = val;
  }

  /**
   * @return date part
   */
  public String getDate() {
    return date;
  }

  /**
   * @param val
   */
  public void setDate(final String val) {
    date = val;
  }

  /**
   * @return time part
   */
  public String getTime() {
    return time;
  }

  /**
   * @param val
   */
  public void setTime(final String val) {
    time = val;
  }

  /**
   * @return utc form of time
   */
  public String getUtc() {
    return utc;
  }

  /**
   * @param val
   */
  public void setUtc(final String val) {
    utc = val;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param val
   */
  public void setLocation(final String val) {
    location = val;
  }

  /**
   * @return summary for event
   */
  public String getSummary() {
    return summary;
  }

  /**
   * @param val
   */
  public void setSummary(final String val) {
    summary = val;
  }

  /** XXX
   * @return what?
   */
  public String getQuery() {
    return query;
  }

  /**
   * @param val
   */
  public void setQuery(final String val) {
    query = val;
  }


  /* ====================================================================
   *                   Object methods
   * ==================================================================== */

  @Override
  public int compareTo(final Event that) {
    int c = getUtc().compareTo(that.getUtc());

    if (c != 0) {
      return c;
    }

    return getHref().compareTo(that.getHref());
  }

  @Override
  public int hashCode() {
    return getHref().hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    return ((Event)o).getHref().equals(getHref());
  }

}
