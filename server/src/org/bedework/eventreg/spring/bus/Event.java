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

package org.bedework.eventreg.spring.bus;

public class Event {
  private String eventGUID;
  private String eventType;
  private String empacKey;
  private String regDeadlineStr;
  private String eventDateStr;
  private String utcStr;
  private String eventTimeStr;
  private String eventLocation;
  private String eventSummary;
  private String subid;
  private String calPath;
  private String recurrenceId;
  private String queryStr;
  private int ticketsAllowed;
  private int totalRegistrants;
  private int ticketsRequested;

  public String getEventGUID() {
    return eventGUID;
  }

  public void setEventGUID(final String eventGUID) {
    this.eventGUID = eventGUID;
  }

  public int getTicketsAllowed() {
    return ticketsAllowed;
  }

  public void setTicketsAllowed(final int ticketsAllowed) {
    this.ticketsAllowed = ticketsAllowed;
  }

  public int getTicketsRequested() {
    return ticketsRequested;
  }

  public void setTicketsRequested(final int ticketsRequested) {
    this.ticketsRequested = ticketsRequested;
  }

  public int getTotalRegistrants() {
    return totalRegistrants;
  }

  public void setTotalRegistrants(final int totalRegistrants) {
    this.totalRegistrants = totalRegistrants;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(final String eventType) {
    this.eventType = eventType;
  }

  public String getEmpacKey() {
    return empacKey;
  }

  public void setEmpacKey(final String empacKey) {
    this.empacKey = empacKey;
  }

  public String getRegDeadlineStr() {
    return regDeadlineStr;
  }

  public void setRegDeadlineString(final String regDeadlineStr) {
    this.regDeadlineStr = regDeadlineStr;
  }

  public String getEventDateStr() {
    return eventDateStr;
  }

  public void setEventDateStr(final String eventDateStr) {
    this.eventDateStr = eventDateStr;
  }

  public String getEventTimeStr() {
    return eventTimeStr;
  }

  public void setEventTimeStr(final String eventTimeStr) {
    this.eventTimeStr = eventTimeStr;
  }

  public String getEventLocation() {
    return eventLocation;
  }

  public void setEventLocation(final String eventLocation) {
    this.eventLocation = eventLocation;
  }

  public String getEventSummary() {
    return eventSummary;
  }

  public void setEventSummary(final String eventSummary) {
    this.eventSummary = eventSummary;
  }

  public String getSubid() {
    return subid;
  }

  public void setSubid(final String subid) {
    this.subid = subid;
  }

  public String getCalPath() {
    return calPath;
  }

  public void setCalPath(final String calPath) {
    this.calPath = calPath;
  }

  public String getRecurrenceId() {
    return recurrenceId;
  }

  public void setRecurrenceId(final String recurrenceId) {
    this.recurrenceId = recurrenceId;
  }

  public String getQueryStr() {
    return queryStr;
  }

  public void setQueryStr(final String queryStr) {
    this.queryStr = queryStr;
  }

  public String getUtcStr() {
    return utcStr;
  }

  public void setUtcStr(final String utcStr) {
    this.utcStr = utcStr;
  }
}
