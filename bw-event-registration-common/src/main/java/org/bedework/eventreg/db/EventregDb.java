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

import org.bedework.base.exc.BedeworkException;
import org.bedework.database.db.DbSession;
import org.bedework.database.db.DbSessionFactoryProvider;
import org.bedework.database.db.DbSessionFactoryProviderImpl;
import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.common.RegistrationInfo;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.misc.Util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** This class manages the Event registration database.
 *
 * @author Mike Douglass
 */
public class EventregDb implements AutoCloseable, Logged, Serializable {
  /* Value used to create a registration. Each running jvm has its
     own batch of these which it renews when empty.
   */
  private static final int defaultRegidBatchSize = 50;

  private static final AtomicLong nextRegid = new AtomicLong();

  /** Last one we can allocate - if negative we haven't got a batch
   */
  private static long lastRegid = -1;

  /** */
  protected boolean open;

  /** When we were created for debugging */
  protected Timestamp objTimestamp;

  /* Factory used to obtain a session
   */
  private static DbSessionFactoryProvider factoryProvider;

  /** Current orm session - exists only across one user interaction
   */
  protected DbSession sess;

  private EventregProperties sysInfo;

  /**
   *
   */
  public EventregDb() {
  }

  /**
   * @return next id
   */
  public Long getNextRegistrationId() {
    synchronized (nextRegid) {
      int attempts = 0;

      while ((lastRegid < 0) ||
              (nextRegid.longValue() > lastRegid)) {
        /* Haven't got ourselves a batch yet or we ran out of ids.
         * This may take a few tries and we need to discard the db on
         * failure.
         */

        final IdBatch batch = getIdBatch(sysInfo);

        if (batch != null) {
          /* We now have a batch.
           */
          nextRegid.set(batch.start);
          lastRegid = batch.end;

          break;
        }

        warn("Error trying to get regid batch after " +
                     attempts + " tries.");
        attempts++;
        final long wait = attempts * 500L;
        warn("Retrying in " + wait + " millisecs");
        try {
          nextRegid.wait(wait);
        } catch (final InterruptedException ie) {
          return null; // Presumably shutting down
        }
      }
    }

    return nextRegid.getAndIncrement();
  }

  public void setSysInfo(final EventregProperties sysInfo) {
    this.sysInfo = sysInfo;
  }

  /**
   * @return true if we had to open it. False if already open
   */
  public boolean open() {
    if (isOpen()) {
      return false;
    }

    openSession();
    open = true;
    return true;
  }

  /**
   * @return true for open
   */
  public boolean isOpen() {
    return open;
  }

  /**
   * @return true if we had did rollback.
   */
  public boolean rollback() {
    if (!isOpen()) {
      return false;
    }

    rollbackTransaction();
    return true;
  }

  @Override
  public void close() {
    boolean ok = true;

    if (debug()) {
      debug("Closing EventregDb: callers: ");
      for (final var caller: Util.getCallers(2)) {
        debug("  " + caller);
      }
    }

    try {
      endTransaction();
    } catch (final Throwable t) {
      ok = false;
      try {
        rollbackTransaction();
      } catch (final Throwable ignored) {}
      error(t);
    } finally {
      try {
        closeSession();
      } catch (final Exception wde1) {
        ok = false;
      }
      open = false;
    }

    if (!ok) {
      throw new EventregException("Failed to close db session");
    }
  }

  /**
   * @param ignoreErrors flag
   * @return false if error occurred
   */
  public boolean close(final boolean ignoreErrors) {
    boolean ok = true;

    try {
      endTransaction();
    } catch (final Throwable t) {
      ok = false;
      try {
        rollbackTransaction();
      } catch (final Throwable ignored) {}

      if (!ignoreErrors) {
        error(t);
      }
    } finally {
      try {
        closeSession();
      } catch (final Exception wde1) {
        ok = false;
      }
      open = false;
    }

    return ok;
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

    sb.append("select chg from Change chg ");
    if (ts != null) {
      sb.append("where chg.lastmod>:lm ");
    }
    sb.append("order by chg.lastmod");

    createQuery(sb.toString());
    if (ts != null) {
      setString("lm", ts);
    }

    return (List<Change>)getList();
  }

  /* =======================================================
   *                   Registrations Object methods
   * ====================================================== */

  public Registration getNewRegistration() {
    return new RegistrationImpl();
  }

  /**
   * @return list of registrations
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getAllRegistrations() {
    createQuery("select reg from Registration reg");

    return (List<Registration>)getList();
  }

  /** Registrations for a user.
   *
   * @param id Long
   * @return a matching registration
   */
  public Registration getByid(final Long id) {
    createQuery(regIdQuery);
    setLong("id", id);

    return (Registration)getUnique();
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
    createQuery(getByUserQuery);
    setString("user", user);
    setString("type", Registration.typeRegistered);

    return (List<Registration>)getList();
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
    createQuery(getUserRegistrationQuery);
    setString("href", eventHref);
    setString("user", user);
    setString("type", RegistrationImpl.typeRegistered);

    return (Registration)getUnique();
  }

  private final static String getByEventQuery =
          "select reg from RegistrationImpl reg " +
                  "where reg.href=:href";

  /** Registrations for an event.
   *
   * @param href to identify event
   * @return matching registrations
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getByEvent(final String href) {
    createQuery(getByEventQuery);
    setString("href", href);

    return (List<Registration>)getList();
  }

  private final static String getRegistrantCountQuery =
          "select count(*) from RegistrationImpl reg " +
                  "where reg.href=:href " +
                  "and reg.type=:type";

  /**
   * @param eventHref to identify event
   * @return number of registration entries for that event
   */
  public long getRegistrantCount(final String eventHref) {
    createQuery(getRegistrantCountQuery);
    setString("href", eventHref);
    setString("type", Registration.typeRegistered);

    @SuppressWarnings("unchecked")
    final Collection<Long> counts = (Collection<Long>)getList();

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
    createQuery(getRegTicketCountQuery);
    setString("href", eventHref);

    @SuppressWarnings("unchecked")
    final List<Integer> cts = (List<Integer>)getList();

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
    createQuery(getWaitingTicketCountQuery);
    setString("href", eventHref);

    final Long ct = (Long)getUnique();
    if (debug()) {
      debug("Count returned " + ct);
    }
    if (ct == null) {
      return 0;
    }

    return Math.max(0, ct - getTicketCount(eventHref));
  }

  private final String getWaitingQuery =
          "select reg from RegistrationImpl reg " +
                  "where reg.href=:href " +
                  "and size(reg.tickets) < reg.ticketsRequested " +
                  "order by reg.waitqDate";

  /**
   * @param eventHref to identify event
   * @return registrations on the waiting list for the event
   */
  public List<Registration> getWaiting(final String eventHref) {
    createQuery(getWaitingQuery);
    setString("href", eventHref);

    return (List<Registration>)getList();
  }

  private final String getTicketCountQuery =
          "select count(*) from TicketImpl tkt " +
                  "where tkt.href=:href";

  /**
   * @param eventHref to identify event
   * @return total number of registration entries for that event, including waiting list
   */
  public long getTicketCount(final String eventHref) {
    createQuery(getTicketCountQuery);
    setString("href", eventHref);

    final Long ct = (Long)getUnique();
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
                  "where tkt.href=:href " +
                  "and tkt.authid=:user";

  /**
   * @param eventHref to identify event
   * @param user to identify user
   * @return number of registration entries for that event and user
   */
  public long getUserTicketCount(final String eventHref,
                                 final String user) {
    createQuery(getUserTicketCountQuery);
    setString("href", eventHref);
    setString("user", user);
    @SuppressWarnings("unchecked")
    final Collection<Long> counts = (Collection<Long>)getList();

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
    createQuery(getCalSuiteFormsQuery);
    setString("owner", calsuite);

    return (List<FormDef>)getList();
  }

  private final static String getCalSuiteFormQuery =
          "select form from FormDef form " +
                  "where form.owner=:owner " +
                  "and form.formName=:formName";

  public FormDef getCalSuiteForm(final String formName,
                                 final String calsuite) {
    createQuery(getCalSuiteFormQuery);
    setString("formName", formName);
    setString("owner", calsuite);

    return (FormDef)getUnique();
  }

  /* =========================================================
   *                   Dbitem methods
   * ========================================================= */

  /** Add the item.
   *
   * @param val the dbitem
   */
  public void add(final Object val) {
    if (!(val instanceof DbItem<?>)) {
      throw new EventregException("Not a dbitem: " + val.getClass());
    }
    try {
      sess.add(val);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  /** Update the persisted state of the item.
   *
   * @param val the dbitem
   */
  public void update(final Object val) {
    if (!(val instanceof DbItem<?>)) {
      throw new EventregException("Not a dbitem: " + val.getClass());
    }
    try {
      sess.update(val);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  /** Delete the dbitem.
   *
   * @param val the dbitem
   */
  public void delete(final Object val) {
    if (!(val instanceof DbItem<?>)) {
      throw new EventregException("Not a dbitem: " + val.getClass());
    }
    final boolean opened = open();

    try {
      sess.delete(val);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    } finally {
      if (opened) {
        close();
      }
    }
  }

  /* =========================================================
   *                   Session methods
   * ========================================================= */

  protected void checkOpen() {
    if (!isOpen()) {
      throw new EventregException("Session call when closed");
    }
  }

  protected synchronized void openSession() {
    if (isOpen()) {
      throw new EventregException("Already open");
    }

    try {
      if (factoryProvider == null) {
        factoryProvider =
                new DbSessionFactoryProviderImpl()
                        .init(sysInfo.getOrmProperties());
      }

      open = true;

      if (sess != null) {
        warn("Session is not null. Will close");
        try {
          close();
        } catch (final Throwable ignored) {
        }
      }

      if (sess == null) {
        if (debug()) {
          debug("New orm session for " + objTimestamp);
        }
        sess = factoryProvider.getNewSession();

        if (debug()) {
          debug("Open session for " + objTimestamp);
        }
      }
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }

    beginTransaction();
  }

  protected synchronized void closeSession() {
    if (!isOpen()) {
      if (debug()) {
        debug("Close for " + objTimestamp + " closed session");
      }
      return;
    }

    if (debug()) {
      debug("Close for " + objTimestamp);
    }

    try {
      if (sess != null) {
        if (sess.rolledback()) {
          sess = null;
          return;
        }

        if (sess.transactionStarted()) {
          sess.rollback();
        }

        sess.close();
      }
    } catch (final Throwable t) {
      try {
        sess.rollback();
      } catch (final Throwable ignored) {}
      try {
        sess.close();
      } catch (final Throwable ignored) {}
    } finally {
      sess = null;
      open = false;
    }
  }

  protected void beginTransaction() {
    checkOpen();

    if (debug()) {
      debug("Begin transaction for " + objTimestamp);
    }
    try {
      sess.beginTransaction();
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  protected void endTransaction() {
    checkOpen();

    if (debug()) {
      debug("End transaction for " + objTimestamp);
    }

    try {
      if (!sess.rolledback()) {
        sess.commit();
      }
    } catch (final BedeworkException be) {
      try {
        sess.rollback();
      } catch (final BedeworkException ignored) {
      }
      throw new EventregException(be);
    }
  }

  protected void rollbackTransaction() {
    try {
      checkOpen();
      sess.rollback();
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  /* =====================================================
   *                   protected methods
   * ===================================================== */

  protected void createQuery(final String query) {
    try {
      sess.createQuery(query);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  protected void setString(final String name,
                         final String value) {
    try {
      sess.setString(name, value);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  protected void setLong(final String name,
                       final Long value) {
    try {
      sess.setLong(name, value);
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  protected Object getUnique() {
    try {
      return sess.getUnique();
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  protected List<?> getList() {
    try {
      return sess.getList();
    } catch (final BedeworkException be) {
      throw new EventregException(be);
    }
  }

  private static final String maxRegistrationIdQuery =
          "select max(registrationId) from RegistrationImpl reg";

  /**
   * @return max registrationid
   */
  protected Long getMaxRegistrationId() {
    createQuery(maxRegistrationIdQuery);

    return (Long)getUnique();
  }

  private static final String regIdQuery =
          "select ri from RegId ri";

  /**
   * @return max registrationid
   */
  protected RegId getRegId() {
    createQuery(regIdQuery);

    return (RegId)getUnique();
  }

  public final static class IdBatch {
    Long start;
    Long end;
  }

  public static IdBatch getIdBatch(final EventregProperties sysInfo) {
    /* This may take a few tries and we need to discard the db.
     */

    final String op;
    final EventregDb nidDb = new EventregDb();

    long regidBatchSize = sysInfo.getRegidBatchSize();

    if (regidBatchSize == 0) {
      regidBatchSize = defaultRegidBatchSize;
    }

    nidDb.setSysInfo(sysInfo);

    try {
      nidDb.open();

      RegId regId = nidDb.getRegId();

      final IdBatch batch = new IdBatch();

      if (regId == null) {
        /* First time I guess
             We need to reserve ourselves a batch of ids. Note we may
             be doing this on top of a system that didn't have the
             nextid table so move past any already allocated ids.
           */

        batch.start = nidDb.getMaxRegistrationId();

        if (batch.start == null) {
          // Empty system
          batch.start = (long)1;
        } else {
          batch.start += 100; // Leave a gap for
        }

        regId = new RegId();
        op = "add";
      } else {
        batch.start = regId.getNextRegistrationId();
        op = "update";
      }

      batch.end = batch.start + regidBatchSize - 1; // Last one we allocate

      regId.setNextRegistrationId(
              batch.end + 1); // Next one for someone else

      try {
        if ("add".equals(op)) {
          nidDb.add(regId);
        } else {
          nidDb.update(regId);
        }
      } catch (final Throwable t) {
        nidDb.warn("Exception trying to " + op +
                           " id batch record");
        nidDb.warn("Message was " + t.getMessage());
        return null;
      }

      return batch;
    } finally {
      try {
        nidDb.close();
      } catch (final Throwable ignored) {}
    }
  }

  /* ====================================================
   *                   Logged methods
   * ==================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
