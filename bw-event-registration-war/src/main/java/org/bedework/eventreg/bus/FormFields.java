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

import org.bedework.eventreg.db.FieldDef;
import org.bedework.util.misc.Logged;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Handle fields for form definition and use.
 *
 * @author douglm
 */
public class FormFields extends Logged
        implements Iterator<FieldDef>, Iterable<FieldDef> {
  private final Set<FieldDef> fields;

  final List<FieldDef> formEls = new ArrayList<>();

  final Iterator<FieldDef> formElsIt;

  private FieldDef lastFieldDef;

  public FormFields(final Set<FieldDef> fields) {
    this.fields = fields;

    for (final FieldDef fd: fields) {
      if (fd.getType().equals(FieldDef.fieldTypeOption)) {
        continue;
      }

      formEls.add(fd);
    }

    Collections.sort(formEls, orderComparator);

    formElsIt = formEls.iterator();
  }

  public int size() {
    if (formEls == null) {
      return 0;
    }

    return formEls.size();
  }

  public Set<FieldDef> getFields() {
    return fields;
  }

  public FieldDef getField(final String fieldName) {
    for (final FieldDef fd: fields) {
      if (fd.getName().equals(fieldName)) {
        return fd;
      }
    }

    return null;
  }

  public List<FieldDef> getFormEls() {
    return formEls;
  }

  public List<FieldDef> getRequiredFormEls() {
    final List<FieldDef> formEls = new ArrayList<>();

    for (final FieldDef fd: fields) {
      if (fd.getType().equals(FieldDef.fieldTypeOption)) {
        continue;
      }

      if (!fd.getRequired()) {
        continue;
      }

      formEls.add(fd);
    }

    return formEls;
  }

  private static Comparator<FieldDef> orderComparator =
          new Comparator<FieldDef>() {
            public int compare(final FieldDef f1,
                               final FieldDef f2) {
              return new Integer(f1.getOrder()).compareTo(
                      new Integer(f2.getOrder()));
    }
  };

  public List<FieldDef> getOptions() {
    final List<FieldDef> options = new ArrayList<>();

    if (lastFieldDef == null) {
      return options;
    }

    for (final FieldDef fd: fields) {
      if (!fd.getType().equals(FieldDef.fieldTypeOption)) {
        continue;
      }

      if (!fd.getGroup().equals(lastFieldDef.getName())) {
        continue;
      }

      options.add(fd);
    }

    Collections.sort(options, orderComparator);

    return options;
  }

  @Override
  public boolean hasNext() {
    return formElsIt.hasNext();
  }

  @Override
  public FieldDef next() {
    lastFieldDef = formElsIt.next();

    return lastFieldDef;
  }

  public void remove() {
    throw new UnsupportedOperationException("remove");
  }

  @Override
  public Iterator<FieldDef> iterator() {
    return this;
  }
}
