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


import edu.rpi.cmt.db.hibernate.HibException;
import edu.rpi.cmt.db.hibernate.HibSession;
import edu.rpi.cmt.db.hibernate.HibSessionFactory;
import edu.rpi.cmt.db.hibernate.HibSessionImpl;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/** This class manages the Exchange synch database.
 *
 * @author Mike Douglass
 */
public class EventregDb implements Serializable {
  private transient Logger log;

  private final boolean debug;

  /** */
  protected boolean open;

  /** When we were created for debugging */
  protected Timestamp objTimestamp;

  /** Current hibernate session - exists only across one user interaction
   */
  protected HibSession sess;

  /**
   *
   */
  public EventregDb() {
    debug = getLogger().isDebugEnabled();
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

  /* ====================================================================
   *                   System info methods
   * ==================================================================== */

  /**
   * @return SysInfo
   * @throws Throwable
   */
  public SysInfo getSys() throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(SysInfo.class.getName());

      sess.createQuery(sb.toString());

      @SuppressWarnings("unchecked")
      List<SysInfo> scs = sess.getList();

      if (scs.size() == 0) {
        return null;
      }

      if (scs.size() == 1) {
        return scs.get(0);
      }

      throw new Exception("Expect only 1 sysinfo entry");
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Add the system info.
   *
   * @param s
   * @throws Throwable
   */
  public void addSys(final SysInfo s) throws Throwable {
    try {
      sess.save(s);
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Update the persisted state of the system.
   *
   * @param s
   * @throws Throwable
   */
  public void updateSys(final SysInfo s) throws Throwable {
    try {
      sess.update(s);
    } catch (HibException he) {
      throw new Exception(he);
    }
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
  public List<Change> getChanges(final String ts) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Change.class.getName());
      sb.append(" chg where ch.lastmod>:lm");

      sess.createQuery(sb.toString());
      sess.setString("lm", ts);

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
      sb.append(" reg where reg.ticketid=:id");

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
  public List<Registration> getByUser(final String user) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.authid=:user");

      sess.createQuery(sb.toString());
      sess.setString("user", user);

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

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);
      sess.setString("user", user);

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
   * @return max ticketid
   * @throws Throwable
   */
  public Long getMaxTicketId() throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select max(ticketid) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg");

      sess.createQuery(sb.toString());

      return (Long)sess.getUnique();
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

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);
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

      sb.append("select sum(numTickets) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");
      sb.append(" and (reg.type <> 'waiting' or reg.type is null)");
      // is null a quick fix; reg.type should not be null

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);

      /*
      Collection<Long> counts = sess.getList();

      long total = 0;

      for (Long l: counts) {
        total += l;
      }*/

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

  /**
   * @param eventHref
   * @return number of registrations on the waiting list for the event
   * @throws Throwable
   */
  public long getWaitingTicketCount(final String eventHref) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select sum(numTickets) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");
      sb.append(" and (reg.type = 'waiting')");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);

      /*
      Collection<Long> counts = sess.getList();

      long total = 0;

      for (Long l: counts) {
        total += l;
      }*/

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
  
  /**
   * @param eventHref
   * @return total number of registration entries for that event, including waiting list
   * @throws Throwable
   */
  public long getTicketCount(final String eventHref) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select sum(numTickets) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);

      /*
      Collection<Long> counts = sess.getList();

      long total = 0;

      for (Long l: counts) {
        total += l;
      }*/

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

  /**
   * @param eventHref
   * @param user
   * @return number of registration entries for that event and user
   * @throws Throwable
   */
  public long getUserTicketCount(final String eventHref,
                                 final String user) throws Throwable {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("select sum(numtickets) from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.href=:href");
      sb.append(" and reg.authid=:user");

      sess.createQuery(sb.toString());
      sess.setString("href", eventHref);
      sess.setString("user", user);
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

  /** Add the registration.
   *
   * @param reg
   * @throws Exception
   */
  public void add(final Registration reg) throws Exception {
    try {
      sess.save(reg);
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Update the persisted state of the registration.
   *
   * @param reg
   * @throws Exception
   */
  public void update(final Registration reg) throws Exception {
    try {
      sess.update(reg);
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Delete the registration.
   *
   * @param reg
   * @throws Throwable
   */
  public void delete(final Registration reg) throws Throwable {
    boolean opened = open();

    try {
      sess.delete(reg);
    } catch (HibException he) {
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
        sess.init(HibSessionFactory.getSessionFactory(), getLogger());
      } catch (HibException he) {
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
   * @param t
   */
  protected void error(final Throwable t) {
    getLogger().error(this, t);
  }

  /**
   * @param msg
   */
  protected void warn(final String msg) {
    getLogger().warn(msg);
  }

  /**
   * @param msg
   */
  protected void trace(final String msg) {
    getLogger().debug(msg);
  }

  /* ====================================================================
   *                   private methods
   * ==================================================================== */

}
