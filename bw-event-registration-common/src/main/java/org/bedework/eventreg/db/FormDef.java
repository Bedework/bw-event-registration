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

import org.bedework.eventreg.common.EventregException;
import org.bedework.base.ToString;

import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;

/** A registration to an event for a single person (or entity)
 *
 * @author douglm
 *
 */
public class FormDef extends DbItem<FormDef> {
  private String formName;
  private String owner;
  private boolean committed;
  private boolean disabled;
  private String created;
  private String lastmod;
  private String comment;
  private Set<FieldDef> fields;

  /**
   *
   */
  public FormDef() {
  }

  /**
   * @param val name
   */
  public void setFormName(final String val) {
    formName = val;
  }

  /**
   * @return name
   */
  public String getFormName() {
    return formName;
  }

  /**
   * @param val the owner
   */
  public void setOwner(final String val) {
    owner = val;
  }

  /**
   * @return owner
   */
  public String getOwner() {
    return owner;
  }

  public void setCommitted(final boolean val) {
    committed = val;
  }

  public boolean getCommitted() {
    return committed;
  }

  public void setDisabled(final boolean val) {
    disabled = val;
  }

  public boolean getDisabled() {
    return disabled;
  }

  /**
   * @param val date
   */
  public void setCreated(final String val) {
    created = val;
  }

  /**
   * @return created
   */
  public String getCreated() {
    return created;
  }

  /**
   * @param val date
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
   * @param val a comment
   */
  public void setComment(final String val) {
    comment = val;
  }

  /**
   * @return comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * @param val set of FieldDefs
   */
  public void setFields(final Set<FieldDef> val) {
    fields = val;
  }

  /**
   * @return FieldDefs
   */
  public Set<FieldDef> getFields() {
    return fields;
  }

  /* ====================================================================
   *                   Property fields
   * ==================================================================== */

  /**
   * @param val a wait list limit - as an integer or an integer followed by "%"
   */
  public void setWaitListLimit(final String val) {
    // validate
    final String checkVal;
    if (val.endsWith("%")) {
      checkVal = val.substring(0, val.length() - 1);
    } else {
      checkVal = val;
    }
    
    try {
      Integer.valueOf(checkVal);
    } catch (final Throwable ignored) {
      throw new EventregException("invalid wait list limit" + val);
    }
    setString("waitListLimit", val);
  }

  /**
   * @return a wait list limit - as an integer or an integer followed by "%"
   */
  public String getWaitListLimit() {
    return may("waitListLimit");
  }

  /*  *
   * @param val flag to say we sent a cancel message
   * /
  public void setCancelSent(final boolean val) {
    setBoolean("cancelSent", val);
  }

  /**
   * @return flag to say we sent a cancel message
   * /
  public boolean getCancelSent() {
    return mayBool("cancelSent");
  }
*/

  /* ====================================================================
   *                   Non db fields
   * ==================================================================== */

  /* ====================================================================
   *                   Convenience methods
   * ==================================================================== */

  /**
   * @param val a FieldDef
   */
  public void addField(final FieldDef val) {
    Set<FieldDef> ts = getFields();

    if (ts == null) {
      ts = new TreeSet<FieldDef>();
      setFields(ts);
    }

    ts.add(val);
  }

  /**
   * @param val FieldDef
   */
  public void removeField(final FieldDef val) {
    final Set<FieldDef> ts = getFields();

    if (ts == null) {
      return;
    }

    ts.remove(val);
  }

  /** Set the various timestamps to now for a new registration.
   *
   */
  public void setTimestamps() {
    final Timestamp sqlDate =
            new Timestamp(new java.util.Date().getTime());

    setCreated(sqlDate.toString());
    setLastmod(getCreated());
  }

  /** Set the lastmod to now.
   *
   */
  public void setLastmod() {
    final Timestamp sqlDate =
            new Timestamp(new java.util.Date().getTime());

    setLastmod(sqlDate.toString());
  }

  /** Add our stuff to the ToString
   *
   * @param ts    for result
   */
  @Override
  protected void toStringSegment(final ToString ts) {
    try {
      super.toStringSegment(ts);
      ts.append("formName", getFormName());
      ts.append("owner", getOwner());
      ts.append("committed", getCommitted());
      ts.append("created", getCreated());
      ts.append("lastmod", getLastmod());
      ts.append("comment", getComment());
    } catch (final Throwable t) {
      ts.append("exception", t.getLocalizedMessage());
    }
  }

  /* =======================================================
   *                   Object methods
   * The following are required for a db object.
   * ======================================================= */

  @Override
  public int compareTo(final FormDef that) {
    final int cmp = getFormName().compareTo(that.getFormName());
    if (cmp != 0) {
      return cmp;
    }
    return getOwner().compareTo(that.getOwner());
  }

  @Override
  public int hashCode() {
    return getFormName().hashCode() * getOwner().hashCode();
  }

  @Override
  public String toString() {
    final ToString ts = new ToString(this);

    toStringSegment(ts);

    return ts.toString();
  }
}

