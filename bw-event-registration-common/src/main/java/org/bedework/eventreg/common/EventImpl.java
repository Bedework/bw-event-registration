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

import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.calendar.XcalUtil.TzGetter;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.timezones.TimezonesException;

import ietf.params.xml.ns.icalendar_2.ArrayOfProperties;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DtstartPropType;
import ietf.params.xml.ns.icalendar_2.IntegerPropertyType;
import ietf.params.xml.ns.icalendar_2.LocationPropType;
import ietf.params.xml.ns.icalendar_2.RecurrenceIdPropType;
import ietf.params.xml.ns.icalendar_2.StatusPropType;
import ietf.params.xml.ns.icalendar_2.SummaryPropType;
import ietf.params.xml.ns.icalendar_2.TextPropertyType;
import ietf.params.xml.ns.icalendar_2.UidPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkMaxTicketsPerUserPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkMaxTicketsPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkRegistrationEndPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkRegistrationStartPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkWaitListLimitPropType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

/**
 * @author douglm
 *
 */
public class EventImpl implements Event {
  private final BaseComponentType comp;
  private final String href;

  private final TzGetter tzs;

  /* Everything else derived from comp. */

  private final ArrayOfProperties properties;

  /** An object of this class is present if the property has been searched for
   * in the icalendar data.
   */
  private static abstract class Pinfo <T extends BasePropertyType> {
    List<T> props;

    void addProperty(final T p) {
      getProps().add(p);
    }

    List<T> getProps() {
      if (props == null) {
        props = new ArrayList<>();
      }

      return props;
    }

    int size() {
      return getProps().size();
    }

    T getProp() {
      if (size() != 1) {
        return null;
      }

      return getProps().get(0);
    }

    abstract String getValue();
  }

  private static class TextPinfo extends Pinfo<TextPropertyType> {
    @Override
    String getValue() {
      final TextPropertyType p = getProp();
      if (p == null) {
        return null;
      }

      return p.getText();
    }
  }

  private static class IntPinfo extends Pinfo<IntegerPropertyType> {
    @Override
    String getValue() {
      final IntegerPropertyType p = getProp();
      if (p == null) {
        return null;
      }

      return String.valueOf(p.getInteger());
    }

    public Integer getInt() {
      final IntegerPropertyType p = getProp();
      if (p == null) {
        return null;
      }

      return p.getInteger().intValue();
    }
  }

  private class DateDatetimePinfo extends Pinfo<DateDatetimePropertyType> {
    private String utc;

    private XcalUtil.DtTzid dt;

    @Override
    String getValue() {
      if (utc != null) {
        return utc;
      }

      final DateDatetimePropertyType p = getProp();
      if (p == null) {
        return null;
      }

      if (p.getDateTime() != null) {
        utc = XcalUtil.getUTC(p, tzs);
        return utc;
      }

      utc = XcalUtil.getIcalFormatDateTime(p.getDate()) + "T00:00:00";
      return utc;
    }

    XcalUtil.DtTzid getDt() {
      if (dt != null) {
        return dt;
      }

      final DateDatetimePropertyType p = getProp();
      if (p == null) {
        return null;
      }

      if (p.getDateTime() == null) {
        return null;
      }

      dt = XcalUtil.getDtTzid(p);
      return dt;
    }
  }

  private TextPinfo uid;
  private DateDatetimePinfo recurrenceId;
  private TextPinfo waitListLimit;
  private IntPinfo ticketsAllowed;
  private IntPinfo maxRegistrants;
  private DateDatetimePinfo regEnd;
  private DateDatetimePinfo regStart;
  private TextPinfo location;
  private TextPinfo summary;
  private TextPinfo status;
  private DateDatetimePinfo dtStart;

  /**
   * @param comp component
   * @param href of event
   * @param tzs for timezones
   */
  public EventImpl(final BaseComponentType comp,
                   final String href,
                   final TzGetter tzs) {
    this.comp = comp;
    this.href = href;
    this.tzs = tzs;

    properties = this.comp.getProperties();
  }

  /**
   * @return the href
   */
  public String getHref() {
    return href;
  }

  /**
   * @return the uid
   */
  public String getUid() {
    if (uid == null) {
      uid = new TextPinfo();
      uid.addProperty((UidPropType)findProperty(UidPropType.class));
    }

    return uid.getValue();
  }

  /**
   * @return recurrence id or null
   */
  public String getRecurrenceId() {
    if (recurrenceId == null) {
      recurrenceId = new DateDatetimePinfo();
      recurrenceId.addProperty((RecurrenceIdPropType)findProperty(RecurrenceIdPropType.class));
    }

    return recurrenceId.getValue();
  }

  /**
   * @return wait list limit as String - either an integer absolute limit or a percentage
   */
  public String getWaitListLimit() {
    if (waitListLimit == null) {
      waitListLimit = new TextPinfo();
      waitListLimit.addProperty((XBedeworkWaitListLimitPropType)findProperty(XBedeworkWaitListLimitPropType.class));
    }

    return waitListLimit.getValue();
  }

  /**
   * @return max number of tickets per user
   */
  public int getMaxTicketsPerUser() {
    if (ticketsAllowed == null) {
      ticketsAllowed = new IntPinfo();
      ticketsAllowed.addProperty((XBedeworkMaxTicketsPerUserPropType)findProperty(XBedeworkMaxTicketsPerUserPropType.class));
    }

    final Integer i = ticketsAllowed.getInt();
    if (i == null) {
      return -1;
    }

    return i;
  }

  /**
   * @return max tickets for the whole event?
   */
  public int getMaxTickets() {
    if (maxRegistrants == null) {
      maxRegistrants = new IntPinfo();
      maxRegistrants.addProperty((XBedeworkMaxTicketsPropType)findProperty(XBedeworkMaxTicketsPropType.class));
    }

    final Integer i = maxRegistrants.getInt();
    if (i == null) {
      return -1;
    }

    return  i;
  }

  /**
   * @return the end of registration
   */
  public String getRegistrationEnd() {
    if (regEnd == null) {
      regEnd = new DateDatetimePinfo();
      regEnd.addProperty((XBedeworkRegistrationEndPropType)findProperty(XBedeworkRegistrationEndPropType.class));
    }

    final XcalUtil.DtTzid dt = regEnd.getDt();

    if (dt == null) {
      return null;
    }

    return dt.dt;
  }

  /**
   * @return the tzid for the end of registration
   */
  public String getRegistrationEndTzid() {
    if (regEnd == null) {
      regEnd = new DateDatetimePinfo();
      regEnd.addProperty((XBedeworkRegistrationEndPropType)findProperty(XBedeworkRegistrationEndPropType.class));
    }

    final XcalUtil.DtTzid dt = regEnd.getDt();

    if (dt == null) {
      return null;
    }

    return dt.tzid;
  }

  /**
   * @return the start of registration
   */
  public String getRegistrationStart() {
    if (regStart == null) {
      regStart = new DateDatetimePinfo();
      regStart.addProperty((XBedeworkRegistrationStartPropType)findProperty(XBedeworkRegistrationStartPropType.class));
    }

    final XcalUtil.DtTzid dt = regStart.getDt();

    if (dt == null) {
      return null;
    }

    return dt.dt;
  }

  /**
   * @return the tzid for the start of registration
   */
  public String getRegistrationStartTzid() {
    if (regStart == null) {
      regStart = new DateDatetimePinfo();
      regStart.addProperty((XBedeworkRegistrationStartPropType)findProperty(XBedeworkRegistrationStartPropType.class));
    }

    final XcalUtil.DtTzid dt = regStart.getDt();

    if (dt == null) {
      return null;
    }

    return dt.tzid;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    if (location == null) {
      location = new TextPinfo();
      location.addProperty((LocationPropType)findProperty(LocationPropType.class));
    }

    return location.getValue();
  }

  /**
   * @return summary for event
   */
  public String getSummary() {
    if (summary == null) {
      summary = new TextPinfo();
      summary.addProperty((SummaryPropType)findProperty(SummaryPropType.class));
    }

    return summary.getValue();
  }

  /**
   * @return status of event
   */
  public String getStatus() {
    if (status == null) {
      status = new TextPinfo();
      status.addProperty((StatusPropType)findProperty(StatusPropType.class));
    }

    return status.getValue();
  }

  /**
   * @return recurrence id or null
   */
  public DtstartPropType getDtStartProp() {
    if (dtStart == null) {
      dtStart = new DateDatetimePinfo();
      dtStart.addProperty((DtstartPropType)findProperty(DtstartPropType.class));
    }

    return (DtstartPropType)dtStart.getProp();
  }

  /**
   * @return date part
   */
  public String getDate() {
    getDtStartProp();

    final XcalUtil.DtTzid dt = dtStart.getDt();

    if (dt == null) {
      return null;
    }

    return dt.dt.substring(0, 8);
  }

  /**
   * @return date or date - time with possible tzid
   */
  public String getDateTime() {
    getDtStartProp();

    final XcalUtil.DtTzid dt = dtStart.getDt();

    if (dt == null) {
      return null;
    }

    final StringBuilder sb =
            new StringBuilder(dt.dt.substring(0, 8));

    if (!dt.dateOnly) {
      sb.append(" - ");
      sb.append(dt.dt.substring(9));

      if (dt.tzid != null) {
        sb.append(" ");
        sb.append(dt.tzid);
      }
    }

    return sb.toString();
  }

  /**
   * @return time part
   */
  public String getTime() {
    getDtStartProp();

    final XcalUtil.DtTzid dt = dtStart.getDt();

    if ((dt == null) || dt.dateOnly) {
      return null;
    }

    return dt.dt.substring(9);
  }

  /**
   * @return tzid
   */
  public String getTzid() {
    getDtStartProp();

    final XcalUtil.DtTzid dt = dtStart.getDt();

    if ((dt == null) || dt.dateOnly) {
      return null;
    }

    return dt.tzid;
  }

  /**
   * @return utc form of time
   */
  public String getUtc() {
    getDtStartProp();

    return dtStart.getValue();
  }

  /**
   * @param dt - YYYY-MM-DD or YYYY-MM-DDThh:mm:ss[Z]
   * @param tz - null for floating or UTC
   * @return Date
   */
  public Date getDate(final String dt, final String tz) {
    if (dt == null) {
      return null;
    }

    if (dt.length() == 8) {
      return DateTimeUtil.fromISODate(dt);
    }

    if (dt.endsWith("Z")) {
      return DateTimeUtil.fromISODateTimeUTC(dt);
    }

    if (tz == null) {
      return DateTimeUtil.fromISODateTime(dt);
    }

    try {
      return DateTimeUtil.fromISODateTime(dt, Timezones.getTz(tz));
    } catch (final TimezonesException e) {
      throw new EventregException(e);
    }
  }

  /**
   * @return Date
   */
  public Date getRegistrationEndDate() {
    if (regEnd == null) {
      regEnd = new DateDatetimePinfo();
      regEnd.addProperty((XBedeworkRegistrationEndPropType)findProperty(XBedeworkRegistrationEndPropType.class));
    }

    if (regEnd == null) {
      return null;
    }

    final XcalUtil.DtTzid dt = regEnd.getDt();
    if (dt == null) {
      return null;
    }

    return getDate(dt.dt, dt.tzid);
  }

  /**
   * @return Date
   */
  public Date getRegistrationStartDate() {
    if (regStart == null) {
      regStart = new DateDatetimePinfo();
      regStart.addProperty((XBedeworkRegistrationStartPropType)findProperty(XBedeworkRegistrationStartPropType.class));
    }

    if (regEnd == null) {
      return null;
    }

    final XcalUtil.DtTzid dt = regStart.getDt();
    if (dt == null) {
      return null;
    }

    return getDate(dt.dt, dt.tzid);
  }

  /* ==================================================
   *                   private methods
   * ================================================== */

  private BasePropertyType findProperty(final Class<? extends BasePropertyType> cl) {
    for (final JAXBElement<? extends BasePropertyType> p: properties.getBasePropertyOrTzid()) {
      if (p.getValue().getClass().equals(cl)) {
        return p.getValue();
      }
    }

    return null;
  }

  /* ==================================================
   *                   Object methods
   * ================================================== */

  @Override
  public int compareTo(final Event that) {
    final int c = getUtc().compareTo(that.getUtc());

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
    if (!(o instanceof Event)) {
      return false;
    }
    return ((Event)o).getHref().equals(getHref());
  }

}
