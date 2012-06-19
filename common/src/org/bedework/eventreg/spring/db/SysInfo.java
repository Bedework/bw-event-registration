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

import edu.rpi.cmt.db.hibernate.Dump;
import edu.rpi.cmt.db.hibernate.NoDump;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;


/** Information used by the event registration service -
 * mostly a set of properties
 */
public class SysInfo extends DbItem<SysInfo> implements PropertiesEntity {
  private String rootUsers;

  private Set<BwProperty> properties;

  /**
   *
   */
  public SysInfo() {

  }

  /** Set the root users list. This is a comma separated list of accounts that
   * have superuser status.
   *
   * @param val    String list of accounts
   */
  public void setRootUsers(final String val) {
    rootUsers = val;
  }

  /** Get the root users
   *
   * @return String   root users
   */
  public String getRootUsers() {
    return rootUsers;
  }

  /* ====================================================================
   *                   Convenience methods
   * ==================================================================== */

  /** Get the root users list
   *
   * @return String[]   root users
   */
  public String[] getRootUsersArray() {
    if (getRootUsers() == null) {
      return new String[0];
    }

    return getRootUsers().split(",");
  }

  /* ====================================================================
   *                   Property methods
   * ==================================================================== */

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#setProperties(java.util.Set)
   */
  @Override
  public void setProperties(final Set<BwProperty> val) {
    properties = val;
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#getProperties()
   */
  @Override
  @Dump(collectionElementName = "property", compound = true)
  public Set<BwProperty> getProperties() {
    return properties;
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#getProperties(java.lang.String)
   */
  @Override
  public Set<BwProperty> getProperties(final String name) {
    TreeSet<BwProperty> ps = new TreeSet<BwProperty>();

    if (getNumProperties() == 0) {
      return null;
    }

    for (BwProperty p: getProperties()) {
      if (p.getName().equals(name)) {
        ps.add(p);
      }
    }

    return ps;
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#removeProperties(java.lang.String)
   */
  @Override
  public void removeProperties(final String name) {
    Set<BwProperty> ps = getProperties(name);

    if (ps == null) {
      return;
    }

    for (BwProperty p: ps) {
      removeProperty(p);
    }
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#getNumProperties()
   */
  @Override
  @NoDump
  public int getNumProperties() {
    Collection<BwProperty> c = getProperties();
    if (c == null) {
      return 0;
    }

    return c.size();
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#findProperty(java.lang.String)
   */
  @Override
  public BwProperty findProperty(final String name) {
    Collection<BwProperty> props = getProperties();

    if (props == null) {
      return null;
    }

    for (BwProperty prop: props) {
      if (name.equals(prop.getName())) {
        return prop;
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#addProperty(org.bedework.calfacade.BwProperty)
   */
  @Override
  public void addProperty(final BwProperty val) {
    Set<BwProperty> c = getProperties();
    if (c == null) {
      c = new TreeSet<BwProperty>();
      setProperties(c);
    }

    if (!c.contains(val)) {
      c.add(val);
    }
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#removeProperty(org.bedework.calfacade.BwProperty)
   */
  @Override
  public boolean removeProperty(final BwProperty val) {
    Set<BwProperty> c = getProperties();
    if (c == null) {
      return false;
    }

    return c.remove(val);
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#copyProperties()
   */
  @Override
  public Set<BwProperty> copyProperties() {
    if (getNumProperties() == 0) {
      return null;
    }
    TreeSet<BwProperty> ts = new TreeSet<BwProperty>();

    for (BwProperty p: getProperties()) {
      ts.add(p);
    }

    return ts;
  }

  /* (non-Javadoc)
   * @see org.bedework.calfacade.base.PropertiesEntity#cloneProperties()
   */
  @Override
  public Set<BwProperty> cloneProperties() {
    if (getNumProperties() == 0) {
      return null;
    }
    TreeSet<BwProperty> ts = new TreeSet<BwProperty>();

    for (BwProperty p: getProperties()) {
      ts.add((BwProperty)p.clone());
    }

    return ts;
  }

  /* ====================================================================
   *                   Property convenience methods
   * ==================================================================== */

  /** Set the single valued named property
   *
   * @param name
   * @param val
   */
  public void setProperty(final String name, final String val) {
    BwProperty prop = findProperty(name);

    if (prop == null) {
      prop = new BwProperty(name, val);
      addProperty(prop);
    } else {
      prop.setValue(val);
    }
  }

  /** Get the single valued named property
   *
   * @param name
   * @return String calendar color
   */
  public String getProperty(final String name) {
    BwProperty prop = findProperty(name);

    if (prop == null) {
      return null;
    }

    return prop.getValue();
  }

  /* ====================================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final SysInfo that) {
    return 0; // Only ever 1 of these
  }

  @Override
  public int hashCode() {
    return 1;
  }
}


