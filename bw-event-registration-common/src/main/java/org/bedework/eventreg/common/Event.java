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

import ietf.params.xml.ns.icalendar_2.DtstartPropType;

import java.util.Date;

/**
 * @author douglm
 *
 */
public interface Event extends Comparable<Event> {
  /**
   * @return the href
   */
  String getHref();

  /**
   * @return the uid
   */
  String getUid();

  /**
   * @return recurrence id or null
   */
  String getRecurrenceId();

  /**
   * @return wait list limit as String - either an integer absolute limit or a percentage
   */
  String getWaitListLimit();

  /**
   * @return max number of tickets per user
   */
  int getMaxTicketsPerUser();

  /**
   * @return max tickets for the whole event?
   */
  int getMaxTickets();

  /**
   * @return the end of registration
   */
  String getRegistrationEnd();

  /**
   * @return the tzid for the end of registration
   */
  String getRegistrationEndTzid();

  /**
   * @return the start of registration
   */
  String getRegistrationStart();

  /**
   * @return the tzid for the start of registration
   */
  String getRegistrationStartTzid();

  /**
   * @return the location
   */
  String getLocation();

  /**
   * @return summary for event
   */
  String getSummary();

  /**
   * @return status of event
   */
  String getStatus();

  /**
   * @return recurrence id or null
   */
  DtstartPropType getDtStartProp();

  /**
   * @return date part
   */
  String getDate();

  /**
   * @return date or date - time with possible tzid
   */
  String getDateTime();

  /**
   * @return time part
   */
  String getTime();

  /**
   * @return tzid
   */
  String getTzid();

  /**
   * @return utc form of time
   */
  String getUtc();

  /**
   * @param dt - YYYY-MM-DD or YYYY-MM-DDThh:mm:ss[Z]
   * @param tz - null for floating or UTC
   * @return Date
   */
  Date getDate(String dt, String tz);

  /**
   * @return Date
   */
  Date getRegistrationEndDate();

  /**
   * @return Date
   */
  Date getRegistrationStartDate();
}
