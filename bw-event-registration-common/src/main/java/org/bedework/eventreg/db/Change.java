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

import org.bedework.eventreg.common.Registration;

/**
 * Change log entry
 *
 */
public class Change extends DbItem<Change> {
  private Long registrationId;
  private String authid;
  private String lastmod;
  private String type;
  private String name;
  private String value;

  /** new registration for user */
  public static final String typeNewReg = "New_Reg";

  /** Registration updated by user */
  public static final String typeUpdReg = "Upd_Reg";

  /** Registration deleted */
  public static final String typeDelReg = "Del_Reg";

  /** Added 1 or more tickets to a waiting registration */
  public static final String typeTktAdded = "Tkt_Add";

  /** All desired tickets supplied to a waiting registration */
  public static final String typeRegFulfilled = "Reg_Fullfilled";

  /** */
  public static final String lblUpdNumTickets = "numTickets";

  /* Non db fields */

  private Registration registration;

  /**
   *
   */
  public Change() {
  }

  /**
   * @param val registrationId
   */
  public void setRegistrationId(final Long val) {
    registrationId = val;
  }

  /**
   * @return registrationId
   */
  public Long getRegistrationId() {
    return registrationId;
  }

  /**
   * @param val authid
   */
  public void setAuthid(final String val) {
    authid = val;
  }

  /**
   * @return authid
   */
  public String getAuthid() {
    return authid;
  }

  /**
   * @param val lastmod
   */
  public void setLastmod(final String val) {
    lastmod = val;
  }

  /**
   * @return lastmod
   */
  public String getLastmod() {
    return lastmod;
  }

  /**
   * @param val type of change
   */
  public void setType(final String val) {
    type = val;
  }

  /**
   * @return type of change
   */
  public String getType() {
    return type;
  }

  /**
   * @param val name of changed value
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
   * @param val name of changed value
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

  /* ====================================================
   *                   Non db fields
   * ==================================================== */

  /**
   * @param val event
   */
  public void setRegistration(final Registration val) {
    registration = val;
  }

  /**
   * @return event
   */
  public Registration getRegistration() {
    return registration;
  }

  /* ====================================================
   *                   Builder
   * ==================================================== */

  public static class Builder {
    private final Change change = new Change();

    public Builder authid(final String val) {
      change.setAuthid(val);
      return this;
    }

    public Builder registrationId(final Long val) {
      change.setRegistrationId(val);
      return this;
    }

    public Builder lastmod(final String val) {
      change.setLastmod(val);
      return this;
    }

    public Builder type(final String val) {
      change.setType(val);
      return this;
    }

    public Builder name(final String val) {
      change.setName(val);
      return this;
    }

    public Builder value(final String val) {
      change.setValue(val);
      return this;
    }

    public Change build() {
      return change;
    }
  }

  /* ====================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================== */

  @Override
  public int compareTo(final Change that) {
    // This is wrong
    return getRegistrationId().compareTo(that.getRegistrationId());
  }

  @Override
  public int hashCode() {
    return getRegistrationId().hashCode();
  }
}


