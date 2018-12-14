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
package org.bedework.eventreg.bus;

import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.Registration;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import java.util.List;

/** Handle change logging.
 *
 * @author douglm
 *
 */
public class ChangeManager implements Logged {
  private SessionManager sessMan;

  /** Specify the change by label and value
   */
  public static class ChangeItem {
    private String name;
    private String value;

    /**
     * @param val
     */
    public void setName(final String val) {
      name = val;
    }

    /**
     * @return name of changed value
     */
    public String getName() {
      return name;
    }

    /**
     * @param val
     */
    public void setValue(final String val) {
      value = val;
    }

    /**
     * @return name of changed value
     */
    public String getValue() {
      return value;
    }

    /**
     * @param name
     * @param value
     */
    public ChangeItem(final String name,
                      final String value) {
      this.name = name;
      this.value = value;
    }

    /**
     * @param value
     * @return new change item
     */
    public static ChangeItem makeUpdNumTickets(final int value) {
      return new ChangeItem(Change.lblUpdNumTickets,
                            String.valueOf(value));
    }
  }

  /**
   * @param sessMan
   */
  public ChangeManager(final SessionManager sessMan) {
    this.sessMan = sessMan;
  }

  /**
   * @param c
   * @throws Throwable
   */
  public void addChange(final Change c) throws Throwable {
    sessMan.addChange(c);
  }

  /**
   * @param ts
   * @return changes
   * @throws Throwable
   */
  public List<Change> getChanges(final String ts) throws Throwable {
    return sessMan.getChanges(ts);
  }

  /**
   * @param reg
   * @param type
   * @throws Throwable
   */
  public void addChange(final Registration reg,
                        final String type) throws Throwable {
    Change c = new Change();

    c.setAuthid(sessMan.getCurrentUser());
    c.setRegistrationId(reg.getRegistrationId());
    c.setLastmod(reg.getLastmod());
    c.setType(type);

    addChange(c);
  }

  /**
   * @param reg
   * @param type
   * @param ci
   * @throws Throwable
   */
  public void addChange(final Registration reg,
                        final String type,
                        final ChangeItem ci) throws Throwable {
    Change c = new Change();

    c.setAuthid(sessMan.getCurrentUser());
    c.setRegistrationId(reg.getRegistrationId());
    c.setLastmod(reg.getLastmod());
    c.setType(type);
    c.setName(ci.name);
    c.setValue(ci.value);

    addChange(c);
  }

  /**
   * @param reg
   * @param numTickets
   * @throws Throwable
   */
  public void addTicketsAdded(final Registration reg,
                              final int numTickets) throws Throwable {
    addChange(reg, Change.typeTktAdded,
              ChangeItem.makeUpdNumTickets(reg.getNumTickets()));
  }

  /**
   * @param reg
   * @throws Throwable
   */
  public void addRegFulfilled(final Registration reg) throws Throwable {
    addChange(reg, Change.typeRegFulfilled);
  }

  /**
   * @param reg
   * @throws Throwable
   */
  public void deleteReg(final Registration reg) throws Throwable {
    addChange(reg, Change.typeDelReg);
  }

  /* ====================================================================
   *                   Logged methods
   * ==================================================================== */

  private BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
