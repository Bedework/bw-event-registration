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
package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.db.FieldDef;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class ProcessDeleteField extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    if (!requireCalsuite() || !requireFormName()) {
      return;
    }

    final String fieldName = reqName();
    if (fieldName == null) {
      errorReturn("No field name");
      return;
    }

    try (final var db = getEventregDb()) {
      db.open();

      final var form = getCalSuiteForm();
      if (form == null) {
        return;
      }

      FieldDef fld = null;
      final var flds = form.getFields();

      for (final FieldDef f: flds) {
        if (f.getName().equals(fieldName)) {
          fld = f;
          break;
        }
      }

      if (fld == null) {
        errorReturn("Field not found " + fieldName);
        return;
      }

      flds.remove(fld);
      db.update(form);

      globals.setMessage("ok");
      setSessionAttr("form", form);
      setSessionAttr("fields", new org.bedework.eventreg.db.FormFields(form.getFields()));
      forward("/docs/forms/ajaxTerminator.jsp");
    }
  }
}
