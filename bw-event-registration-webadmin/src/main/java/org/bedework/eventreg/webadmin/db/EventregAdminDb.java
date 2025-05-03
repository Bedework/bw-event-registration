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
package org.bedework.eventreg.webadmin.db;

import org.bedework.base.exc.BedeworkException;
import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.common.RegistrationInfo;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.RegistrationImpl;
import org.bedework.eventreg.db.RegistrationInfoImpl;
import org.bedework.util.calendar.XcalUtil;

import java.util.Collection;
import java.util.List;

/** This class manages the Event registration database for
 *  admin operations.
 *
 * @author Mike Douglass
 */
public class EventregAdminDb extends EventregDb {
  /**
   *
   */
  public EventregAdminDb() {
  }

  /* =====================================================
   *                   Changes methods
   * ===================================================== */

  /**
   * @param c Change
   */
  public void addChange(final Change c) {
    try {
      sess.add(c);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  /**
   * @param ts - timestamp value - get changes after this
   * @return - list of changes since ts
   */
  @SuppressWarnings("unchecked")
  public List<Change> getChanges(final String ts) {
    final StringBuilder sb = new StringBuilder();

    sb.append("select chg from Change chg");
    if (ts != null) {
      sb.append(" where chg.lastmod>:lm");
    }
    sb.append(" order by chg.lastmod");

    createQuery(sb.toString());
    if (ts != null) {
      setString("lm", ts);
    }

    return (List<Change>)getList();
  }

  /* =======================================================
   *                   Registrations Object methods
   * ====================================================== */

  /**
   * @return list of registrations
   */
  public List<Registration> getAllRegistrations() {
    //noinspection unchecked
    return (List<Registration>)createQuery(
            "select reg from RegistrationImpl reg")
            .getList();
  }

  private final static String getByUserQuery =
          "select reg from RegistrationImpl reg " +
                  "where reg.authid=:user " +
                  "and reg.type=:type";

  /** Registrations for a user.
   *
   * @param user to match
   * @return a matching registrations
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getByUser(final String user) {
    return (List<Registration>)createQuery(getByUserQuery)
            .setString("user", user)
            .setString("type", Registration.typeRegistered)
            .getList();
  }

  private final static String getUserRegistrationQuery =
          "select reg from RegistrationImpl reg " +
                  "where reg.href=:href " +
                  "and reg.authid=:user " +
                  "and reg.type=:type";

  /**
   * @param eventHref to identify event
   * @param user to identify user
   * @return registration or null
   */
  public Registration getUserRegistration(final String eventHref,
                                          final String user) {
    return (Registration)createQuery(getUserRegistrationQuery)
            .setString("href", eventHref)
            .setString("user", user)
            .setString("type", RegistrationImpl.typeRegistered)
            .getUnique();
  }

  private final static String getByEventQuery =
          "select reg from RegistrationImpl reg " +
                  "where reg.href=:href";

  /** Registrations for an event.
   *
   * @param href to identify event
   * @return matching registrations
   */
  public List<Registration> getByEvent(final String href) {
    //noinspection unchecked
    return (List<Registration>)createQuery(getByEventQuery)
            .setString("href", href)
            .getList();
  }

  private final static String getRegistrantCountQuery =
          "select count(*) from RegistrationImpl reg " +
                  "where reg.href=:href and " +
                  "reg.type=:type";

  /**
   * @param eventHref to identify event
   * @return number of registration entries for that event
   */
  public long getRegistrantCount(final String eventHref) {
    @SuppressWarnings("unchecked")
    final var counts = (Collection<Long>)createQuery(getRegistrantCountQuery)
            .setString("href", eventHref)
            .setString("type", Registration.typeRegistered)
            .getList();

    long total = 0;

    for (final Long l: counts) {
      total += l;
    }

    return total;
  }

  public RegistrationInfo getRegistrationInfo(final Event event,
                                              final String eventHref) {
    final var rinfo = new RegistrationInfoImpl();

    rinfo.setTicketCount(getRegTicketCount(eventHref));
    rinfo.setMaxTicketCount(event.getMaxTickets());
    rinfo.setMaxTicketsPerUser(event.getMaxTicketsPerUser());
    rinfo.setWaitingTicketCount(getWaitingTicketCount(eventHref));
    rinfo.setWaitListLimit(event.getWaitListLimit());
    rinfo.setRegistrantCount(getRegistrantCount(eventHref));
    rinfo.setRegistrationStart(
            fixDt(event.getRegistrationStart()));
    rinfo.setRegistrationEnd(
            fixDt(event.getRegistrationEnd()));

    return rinfo;
  }

  private String fixDt(final String dt) {
    final var xdt = XcalUtil.getXmlFormatDateTime(dt);

    final var xdatePart = xdt.substring(0, 10);
    final var xtimepart = xdt.substring(11);
    if (!"00:00".equals(xtimepart)) {
      return xdatePart + " " + xtimepart;
    } else {
      return xdatePart;
    }
  }

  private final static String getRegTicketCountQuery =
          "select size(reg.tickets) from RegistrationImpl reg " +
                  "where reg.href=:href";

  /**
   * @param eventHref to identify event
   * @return number of registrations not on the waiting list for the event
   */
  public long getRegTicketCount(final String eventHref) {
    @SuppressWarnings("unchecked")
    final var cts = (List<Integer>)createQuery(getRegTicketCountQuery)
            .setString("href", eventHref)
            .getList();

    if (cts == null) {
      return 0;
    }

    Long ct = (long)0;
    for (final Integer i: cts) {
      ct += i;
    }

    return ct;
  }

  private final static String getWaitingTicketCountQuery =
          "select sum(ticketsRequested) from RegistrationImpl reg " +
                  "where reg.href=:href";

  /**
   * @param eventHref href of event
   * @return number of tickets requested on the waiting list for the event
   */
  public long getWaitingTicketCount(final String eventHref) {
    final var ct = (Long)createQuery(getWaitingTicketCountQuery)
            .setString("href", eventHref)
            .getUnique();

    if (debug()) {
      debug("Count returned " + ct);
    }
    if (ct == null) {
      return 0;
    }

    return Math.max(0, ct - getTicketCount(eventHref));
  }

  private final String getWaitingQuery =
          """
            select reg from RegistrationImpl reg \
            where reg.href=:href and \
              size(reg.tickets) < reg.ticketsRequested \
            order by reg.waitqDate""";

  /**
   * @param eventHref to identify event
   * @return registrations on the waiting list for the event
   */
  public List<Registration> getWaiting(final String eventHref) {
    //noinspection unchecked
    return (List<Registration>)createQuery(getWaitingQuery)
            .setString("href", eventHref)
            .getList();
  }

  private final String getTicketCountQuery =
          "select count(*) from TicketImpl tkt " +
                  "where tkt.href=:href";

  /**
   * @param eventHref to identify event
   * @return total number of registration entries for that event, including waiting list
   */
  public long getTicketCount(final String eventHref) {
    final var ct = (Long)createQuery(getTicketCountQuery)
            .setString("href", eventHref)
            .getUnique();
    if (debug()) {
      debug("Count returned " + ct);
    }
    if (ct == null) {
      return 0;
    }

    return ct;
  }

  private final static String getUserTicketCountQuery =
          "select count(*) from TicketImpl tkt " +
                  "where tkt.href=:href" +
                  " and tkt.authid=:user";

  /**
   * @param eventHref to identify event
   * @param user to identify user
   * @return number of registration entries for that event and user
   */
  public long getUserTicketCount(final String eventHref,
                                 final String user) {
    @SuppressWarnings("unchecked")
    final var counts =
            (Collection<Long>)createQuery(getUserTicketCountQuery)
                    .setString("href", eventHref)
                    .setString("user", user)
                    .getList();

    long total = 0;

    for (final Long l: counts) {
      total += l;
    }

    return total;
  }

  /* ========================================================
   *                   Form definition methods
   * ======================================================== */

  private final static String getCalSuiteFormsQuery =
          "select form from FormDef form " +
                  "where form.owner=:owner";

  public List<FormDef> getCalSuiteForms(final String calsuite) {
    //noinspection unchecked
    return (List<FormDef>)createQuery(getCalSuiteFormsQuery)
            .setString("owner", calsuite)
            .getList();
  }

  private final static String getCalSuiteFormQuery =
          "select form from FormDef form " +
                  "where form.owner=:owner and" +
                  " form.formName=:formName";

  public FormDef getCalSuiteForm(final String formName,
                                 final String calsuite) {
    return (FormDef)createQuery(getCalSuiteFormQuery)
            .setString("formName", formName)
            .setString("owner", calsuite)
            .getUnique();
  }
}
