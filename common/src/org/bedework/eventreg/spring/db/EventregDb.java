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


import edu.rpi.cmt.db.hibernate.HibException;
import edu.rpi.cmt.db.hibernate.HibSession;
import edu.rpi.cmt.db.hibernate.HibSessionFactory;
import edu.rpi.cmt.db.hibernate.HibSessionImpl;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.Timestamp;
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
   * @throws Exception
   */
  public boolean open() throws Exception {
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
   * @throws Exception
   */
  public void close() throws Exception {
    try {
      endTransaction();
    } catch (Exception wde) {
      try {
        rollbackTransaction();
      } catch (Exception wde1) {}
      throw wde;
    } finally {
      try {
        closeSession();
      } catch (Exception wde1) {}
      open = false;
    }
  }

  /* ====================================================================
   *                   User Object methods
   * ==================================================================== */

  /**
   * @param id
   * @return SysInfo
   * @throws Exception
   */
  public SysInfo getSys() throws Exception {
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
   * @throws Exception
   */
  public void addSys(final SysInfo s) throws Exception {
    try {
      sess.save(s);
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Update the persisted state of the system.
   *
   * @param s
   * @throws Exception
   */
  public void updateSys(final SysInfo s) throws Exception {
    try {
      sess.update(s);
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /* ====================================================================
   *                   Registrations Object methods
   * ==================================================================== */

  /**
   * @return list of registrations
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<Registration> getAllRegistrations() throws Exception {
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
   * @param email
   * @return a matching registrations
   * @throws Exception
   */
  public List<Registration> getByEmail(final String email) throws Exception {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.email=:email");

      sess.createQuery(sb.toString());
      sess.setString("email", email);

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /** Registrations for an event.
   *
   * @param email
   * @return a matching registrations
   * @throws Exception
   */
  public List<Registration> getByEvent(final String uid) throws Exception {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Registration.class.getName());
      sb.append(" reg where reg.uid=:uid");

      sess.createQuery(sb.toString());
      sess.setString("uid", uid);

      return sess.getList();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  /* * Find for event and user
   *
   * @param sub
   * @return matching subscriptions
   * @throws Exception
   * /
  public Subscription find(final Subscription sub) throws Exception {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("from ");
      sb.append(Subscription.class.getName());
      sb.append(" sub where sub.endAConnectorInfo.connectorId=:aconnid");
      sb.append(" and sub.endAConnectorInfo.synchProperties=:aconnprops");
      sb.append(" and sub.endBConnectorInfo.connectorId=:bconnid");
      sb.append(" and sub.endBConnectorInfo.synchProperties=:bconnprops");
      sb.append(" and sub.direction=:dir");
      sb.append(" and sub.master=:mstr");

      sess.createQuery(sb.toString());
      sess.setString("aconnid",
                     sub.getEndAConnectorInfo().getConnectorId());
      sess.setString("aconnprops",
                     sub.getEndAConnectorInfo().getSynchProperties());
      sess.setString("bconnid",
                     sub.getEndBConnectorInfo().getConnectorId());
      sess.setString("bconnprops",
                     sub.getEndBConnectorInfo().getSynchProperties());
      sess.setString("dir",
                     sub.getDirection().name());
      sess.setString("mstr",
                     sub.getMaster().name());

      return (Subscription)sess.getUnique();
    } catch (HibException he) {
      throw new Exception(he);
    }
  }*/

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
   * @param sub
   * @throws Exception
   */
  public void delete(final Registration reg) throws Exception {
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

  protected void checkOpen() throws Exception {
    if (!isOpen()) {
      throw new Exception("Session call when closed");
    }
  }

  protected synchronized void openSession() throws Exception {
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

  protected void beginTransaction() throws Exception {
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

  protected void endTransaction() throws Exception {
    checkOpen();

    if (debug) {
      trace("End transaction for " + objTimestamp);
    }

    try {
      if (!sess.rolledback()) {
        sess.commit();
      }
    } catch (HibException he) {
      throw new Exception(he);
    }
  }

  protected void rollbackTransaction() throws Exception {
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
