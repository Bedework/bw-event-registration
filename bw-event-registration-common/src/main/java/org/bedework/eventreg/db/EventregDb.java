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

import org.bedework.eventreg.service.EventregProperties;
import org.bedework.util.hibernate.HibException;
import org.bedework.util.hibernate.HibSession;
import org.bedework.util.hibernate.HibSessionImpl;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/** This class manages the Event registration database.
 *
 * @author Mike Douglass
 */
public class EventregDb implements Serializable {
  private transient Logger log;

  private final boolean debug;

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

  /** Current hibernate session - exists only across one user interaction
   */
  protected HibSession sess;

  private static SessionFactory sessionFactory;

  private EventregProperties sysInfo;

  /**
   *
   */
  public EventregDb() {
    debug = getLogger().isDebugEnabled();
  }

  /**
   * @return next id
   * @throws Throwable
   */
  public Long getNextRegistrationId() throws Throwable {
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
        final long wait = attempts * 500;
        warn("Retrying in " + wait + " millisecs");
        try {
          this.wait(wait);
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
   * @throws Throwable
   */
  public boolean open() throws Throwable {
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
   * @throws Throwable
   */
  public boolean rollback() throws Throwable {
    if (!isOpen()) {
      return false;
    }

    rollbackTransaction();
    return true;
  }

  /**
   * @return false if error occurred
   */
  public boolean close() {
    boolean ok = true;

    try {
      endTransaction();
    } catch (Throwable t) {
      ok = false;
      try {
        rollbackTransaction();
      } catch (Throwable t1) {}
      error(t);
    } finally {
      try {
        closeSession();
      } catch (Exception wde1) {
        ok = false;
      }
      open = false;
    }

    return ok;
  }

  /**
   * @param ignoreErrors
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

  /* ====================================================================
   *                   Changes methods
   * ==================================================================== */

  /**
   * @param c
   * @throws Throwable
   */
  public void addChange(final Change c) throws Throwable {
    try {
      sess.save(c);
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /**
   * @param ts - timestamp value - get changes after this
   * @return - list of changes since ts
   * @throws Throwable
   */
  @SuppressWarnings("unchecked")
  public List<Change> getChanges(final String ts) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Change.class.getName());
      sb.append(" chg");
      if (ts != null) {
        sb.append(" where chg.lastmod>:lm");
      }
      sb.append(" order by chg.lastmod");

      sess.createQuery(sb.toString());
      if (ts != null) {
        sess.setString("lm", ts);
      }

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /* ====================================================================
   *                   Registrations Object methods
   * ==================================================================== */

  /**
   * @return list of registrations
   * @throws Throwable
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getAllRegistrations() throws Throwable {
    StringBuilder sb = new StringBuilder();

    sb.append("from ");
    sb.append(Registration.class.getName());

    try {
      sess.createQuery(sb.toString());

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Registrations for a user.
   *
   * @param id
   * @return a matching registration
   * @throws Throwable
   */
  public Registration getByid(final Long id) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.registrationId=:id");

      sess.createQuery(sb.toString());
      sess.setLong("id", id);

      return (Registration)sess.getUnique();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Registrations for a user.
   *
   * @param user
   * @return a matching registrations
   * @throws Throwable
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getByUser(final String user) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.authid=:user");
      sb.append(" and reg.type=:type");

      sess.createQuery(sb.toString());
      sess.setString("user", user);
      sess.setString("type", Registration.typeRegistered);

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /**
   * @param eventHref
   * @param user
   * @return registration or null
   * @throws Throwable
   */
  public Registration getUserRegistration(final String eventHref,
                                          final String user) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");
      sb.append(" and reg.authid=:user");
      sb.append(" and reg.type=:type");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);
      sess.setString("user", user);
      sess.setString("type", Registration.typeRegistered);

      return (Registration)sess.getUnique();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Registrations for an event.
   *
   * @param href
   * @return a matching registrations
   * @throws Throwable
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getByEvent(final String href) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");

      sess.createQuery(sb.toString());
      sess.setString("href", href);

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /**
   * @param eventHref
   * @return number of registration entries for that event
   * @throws Throwable
   */
  public long getRegistrantCount(final String eventHref) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select count(*) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");
      sb.append(" and reg.type=:type");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);
      sess.setString("type", Registration.typeRegistered);

      @SuppressWarnings("unchecked")
      Collection<Long> counts = sess.getList();

      long total = 0;

      for (Long l: counts) {
        total += l;
      }

      return total;
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /**
   * @param eventHref
   * @return number of registrations not on the waiting list for the event
   * @throws Throwable
   */
  public long getRegTicketCount(final String eventHref) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select size(tickets) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);

      List<Integer> cts = sess.getList();

      if (cts == null) {
        return 0;
      }

      Long ct = (long)0;
      for (Integer i: cts) {
        ct += i;
      }

      return ct;
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  private final static String getWaitingTicketCountQuery =
          "select sum(ticketsRequested) from " +
                  Registration.class.getName() +
                  " reg where reg.href=:href";

  /**
   * @param eventHref href of event
   * @return number of tickets requested on the waiting list for the event
   * @throws Throwable on fatal error
   */
  public long getWaitingTicketCount(final String eventHref) throws Throwable {
    try {
      sess.createQuery(getWaitingTicketCountQuery);
      sess.setString("href", eventHref);

      final Long ct = (Long)sess.getUnique();
      trace("Count returned " + ct);
      if (ct == null) {
        return 0;
      }

      return Math.max(0, ct - getTicketCount(eventHref));
    } catch (final HibException he) {
      throw new Exception(he);
    }
  }

  /**
   * @param eventHref
   * @return registrations on the waiting list for the event
   * @throws Throwable
   */
  public List<Registration> getWaiting(final String eventHref) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");
      sb.append(" and size(reg.tickets) < reg.ticketsRequested");
      sb.append(" order by reg.waitqDate");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /**
   * @param eventHref
   * @return total number of registration entries for that event, including waiting list
   * @throws Throwable
   */
  public long getTicketCount(final String eventHref) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select count(*) from ");
      sb.append(Ticket.class.getName());
      sb.append(" tkt where tkt.href=:href");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);

      Long ct = (Long)sess.getUnique();
      trace("Count returned " + ct);
      if (ct == null) {
        return 0;
      }

      return ct;
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  private final static String getUserTicketCountQuery =
          "select count(*) from " +
                  Ticket.class.getName() +
                  " tkt where tkt.href=:href" +
                  " and tkt.authid=:user";

  /**
   * @param eventHref
   * @param user
   * @return number of registration entries for that event and user
   * @throws Throwable
   */
  public long getUserTicketCount(final String eventHref,
                                 final String user) throws Throwable {
    try {
      sess.createQuery(getUserTicketCountQuery);
      sess.setString("href", eventHref);
      sess.setString("user", user);
      @SuppressWarnings("unchecked")
      Collection<Long> counts = sess.getList();

      long total = 0;

      for (final Long l: counts) {
        total += l;
      }

      return total;
    } catch (final HibException he) {
      throw new Exception(he);
    }
  }

  /* ====================================================================
   *                   Form definition methods
   * ==================================================================== */

  private final static String getCalSuiteFormsQuery =
          "from " + FormDef.class.getName() +
                  " form where form.owner=:owner";

  public List<FormDef> getCalSuiteForms(final String calsuite) throws Throwable {
    sess.createQuery(getCalSuiteFormsQuery);
    sess.setString("owner", calsuite);

    return sess.getList();
  }

  private final static String getCalSuiteFormQuery =
          "from " + FormDef.class.getName() +
                  " form where form.owner=:owner and" +
                  " form.formName=:formName";

  public FormDef getCalSuiteForm(final String formName,
                                       final String calsuite) throws Throwable {
    sess.createQuery(getCalSuiteFormQuery);
    sess.setString("formName", formName);
    sess.setString("owner", calsuite);

    return (FormDef)sess.getUnique();
  }

  /* ====================================================================
   *                   Dbitem methods
   * ==================================================================== */

  /** Add the item.
   *
   * @param val the dbitem
   * @throws Exception
   */
  public void add(final DbItem val) throws Exception {
    try {
      sess.save(val);
    } catch (final HibException he) {
      throw new Exception(he);
    }
  }

  /** Update the persisted state of the item.
   *
   * @param val the dbitem
   * @throws Exception
   */
  public void update(final DbItem val) throws Exception {
    try {
      sess.update(val);
    } catch (final HibException he) {
      throw new Exception(he);
    }
  }

  /** Delete the dbitem.
   *
   * @param val the dbitem
   * @throws Throwable
   */
  public void delete(final DbItem val) throws Throwable {
    final boolean opened = open();

    try {
      sess.delete(val);
    } catch (final HibException he) {
      throw new Exception(he);
    } finally {
      if (opened) {
        close();
      }
    }
  }

  /* ====================================================================
   *                   Session methods
   * ==================================================================== */

  protected void checkOpen() throws Throwable {
    if (!isOpen()) {
      throw new Exception("Session call when closed");
    }
  }

  private SessionFactory getSessionFactory() throws Throwable {
    if (sessionFactory != null) {
      return sessionFactory;
    }

    synchronized (this) {
      if (sessionFactory != null) {
        return sessionFactory;
      }

      /** Get a new hibernate session factory. This is configured from an
       * application resource hibernate.cfg.xml together with some run time values
       */
      final Configuration conf = new Configuration();

      final StringBuilder sb = new StringBuilder();

      @SuppressWarnings("unchecked")
      final List<String> ps = sysInfo.getHibernateProperties();

      for (final String p: ps) {
        sb.append(p);
        sb.append("\n");
      }

      final Properties hprops = new Properties();
      hprops.load(new StringReader(sb.toString()));

      conf.addProperties(hprops).configure();

      sessionFactory = conf.buildSessionFactory();

      return sessionFactory;
    }
  }

  protected synchronized void openSession() throws Throwable {
    if (isOpen()) {
      throw new Exception("Already open");
    }

    open = true;

    if (sess != null) {
      warn("Session is not null. Will close");
      try {
        close();
      } finally {
      }
    }

    if (sess == null) {
      if (debug) {
        trace("New hibernate session for " + objTimestamp);
      }
      sess = new HibSessionImpl();
      try {
        sess.init(getSessionFactory(), getLogger());
      } catch (final HibException he) {
        throw new Exception(he);
      }
      trace("Open session for " + objTimestamp);
    }

    beginTransaction();
  }

  protected synchronized void closeSession() throws Exception {
    if (!isOpen()) {
      if (debug) {
        trace("Close for " + objTimestamp + " closed session");
      }
      return;
    }

    if (debug) {
      trace("Close for " + objTimestamp);
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
//        sess.disconnect();
        sess.close();
        sess = null;
      }
    } catch (Throwable t) {
      try {
        sess.close();
      } catch (Throwable t1) {}
      sess = null; // Discard on error
    } finally {
      open = false;
    }
  }

  protected void beginTransaction() throws Throwable {
    checkOpen();

    if (debug) {
      trace("Begin transaction for " + objTimestamp);
    }
    try {
      sess.beginTransaction();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  protected void endTransaction() throws Throwable {
    checkOpen();

    if (debug) {
      trace("End transaction for " + objTimestamp);
    }

    try {
      if (!sess.rolledback()) {
        sess.commit();
      }
    } catch (HibException he) {
      sess.rollback();
      throw new Exception(he);
    }
  }

  protected void rollbackTransaction() throws Throwable {
    try {
      checkOpen();
      sess.rollback();
    } catch (HibException he) {
      throw new Exception(he);
    } finally {
    }
  }

  /**
   * @return Logger
   */
  protected Logger getLogger() {
    if (log == null) {
      log = Logger.getLogger(this.getClass());
    }

    return log;
  }

  /**
   * @param t exception
   */
  protected void error(final Throwable t) {
    getLogger().error(this, t);
  }

  /**
   * @param msg the message
   */
  protected void warn(final String msg) {
    getLogger().warn(msg);
  }

  /**
   * @param msg the message
   */
  protected void trace(final String msg) {
    getLogger().debug(msg);
  }

  /* ====================================================================
   *                   private methods
   * ==================================================================== */

  private static final String maxRegistrationIdQuery =
          "select max(registrationId) from " +
                  Registration.class.getName() + " reg";

  /**
   * @return max registrationid
   * @throws Throwable
   */
  private Long getMaxRegistrationId() throws Throwable {
    try {
      sess.createQuery(maxRegistrationIdQuery);

      return (Long)sess.getUnique();
    } catch (final HibException he) {
      throw new Exception(he);
    }
  }

  private static final String regIdQuery =
          "from " +
                  RegId.class.getName();

  /**
   * @return max registrationid
   * @throws Throwable
   */
  private RegId getRegId() throws Throwable {
    try {
      sess.createQuery(regIdQuery);

      return (RegId)sess.getUnique();
    } catch (final HibException he) {
      throw new Exception(he);
    }
  }

  private static class IdBatch {
    Long start;
    Long end;
  }

  public static IdBatch getIdBatch(final EventregProperties sysInfo) throws Throwable {
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

}
