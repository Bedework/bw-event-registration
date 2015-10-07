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

import org.bedework.util.misc.ToString;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/** Fields that exists in a form.
 *
 * <p>By field in the daa model we mean almost any element that makes
 * up a form, We distinguish them by type and group using the group
 * property.</p>
 *
 * <p>A drop down has the type select, radio buttons have type radio,
 * and each has a set of options associated with them</p>
 *
 * <p>For example, a select with name "fldname2" might have 2 options,
 * named "fldname3" and "fldname4". Each of those options will have a
 * group name of "fldname2"</p>
 *
 * <p>Deleting field "fldname4" deletes an option only. Deleting field
 * "fldname2" deletes the field and all the associated options.</p>
 *
 * @author douglm
 *
 *
 */
public class FieldDef extends DbItem<FieldDef> {
  public final static String fieldTypeTextArea = "textarea";
  public final static String fieldTypeTextInput = "textinput";
  public final static String fieldTypeCheckbox = "checkbox";

  public final static String fieldTypeRadio = "radio";
  public final static String fieldTypeSelect = "select";

  /** These are effectively sub-fields of another field. */
  public final static String fieldTypeOption = "option";

  public final static Set<String> validTypes;

  static {
    final Set<String> vt = new TreeSet<>();

    vt.add(fieldTypeTextArea);
    vt.add(fieldTypeTextInput);
    vt.add(fieldTypeCheckbox);
    vt.add(fieldTypeRadio);
    vt.add(fieldTypeSelect);
    vt.add(fieldTypeOption);

    validTypes = Collections.unmodifiableSet(vt);
  }

  private String formName;
  private String owner;
  private String name;
  private String type;
  private String label;
  private String value;
  private String description;
  private String group;
  private boolean required;
  private int order;
  private boolean defaultValue;
  private boolean multiValued;
  private String width;
  private String height;

  /**
   *
   */
  public FieldDef() {

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

  /** Field names are unique within a form.
   *
   * @param val name
   */
  public void setName(final String val) {
    name = val;
  }

  /**
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * @param val type
   */
  public void setType(final String val) {
    type = val;
  }

  /**
   * @return type
   */
  public String getType() {
    return type;
  }

  /**
   * @param val name
   */
  public void setLabel(final String val) {
    label = val;
  }

  /**
   * @return name
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param val name
   */
  public void setValue(final String val) {
    value = val;
  }

  /**
   * @return name
   */
  public String getValue() {
    return value;
  }

  /**
   * @param val name
   */
  public void setDescription(final String val) {
    description = val;
  }

  /**
   * @return name
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param val name
   */
  public void setGroup(final String val) {
    group = val;
  }

  /**
   * @return name
   */
  public String getGroup() {
    return group;
  }

  /**
   * @param val name
   */
  public void setRequired(final boolean val) {
    required = val;
  }

  /**
   * @return name
   */
  public boolean getRequired() {
    return required;
  }

  /**
   * @param val name
   */
  public void setOrder(final int val) {
    order = val;
  }

  /**
   * @return order
   */
  public int getOrder() {
    return order;
  }

  /**
   * @param val name
   */
  public void setDefaultValue(final boolean val) {
    defaultValue = val;
  }

  /**
   * @return name
   */
  public boolean getDefaultValue() {
    return defaultValue;
  }

  /**
   * @param val name
   */
  public void setMultivalued(final boolean val) {
    multiValued = val;
  }

  /**
   * @return name
   */
  public boolean getMultivalued() {
    return multiValued;
  }

  /**
   * @param val name
   */
  public void setWidth(final String val) {
    width = val;
  }

  /**
   * @return name
   */
  public String getWidth() {
    return width;
  }

  /**
   * @param val name
   */
  public void setHeight(final String val) {
    height = val;
  }

  /**
   * @return name
   */
  public String getHeight() {
    return height;
  }

  /* ====================================================================
   *                   Non db fields
   * ==================================================================== */

  /* ====================================================================
   *                   Convenience methods
   * ==================================================================== */

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
      ts.append("name", getName());
      ts.append("type", getType());
      ts.append("label", getLabel());
      ts.append("value", getValue());
      ts.append("description", getDescription());
      ts.append("group", getGroup());
      ts.append("required", getRequired());
      ts.append("order", getOrder());
      ts.append("defaultValue", getDefaultValue());
      ts.append("multivalued", getMultivalued());
      ts.append("width", getWidth());
      ts.append("height", getHeight());
    } catch (final Throwable t) {
      ts.append("exception", t.getLocalizedMessage());
    }
  }

  /* ====================================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final FieldDef that) {
    int cmp = getFormName().compareTo(that.getFormName());
    if (cmp != 0) {
      return cmp;
    }
    cmp = getOwner().compareTo(that.getOwner());
    if (cmp != 0) {
      return cmp;
    }
    return getName().compareTo(that.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public String toString() {
    final ToString ts = new ToString(this);

    toStringSegment(ts);

    return ts.toString();
  }
}

