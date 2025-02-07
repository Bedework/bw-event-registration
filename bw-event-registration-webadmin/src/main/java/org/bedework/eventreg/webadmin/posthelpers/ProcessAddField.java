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
package org.bedework.eventreg.webadmin.posthelpers;

import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregInvalidNameException;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.FormFields;
import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;
import org.bedework.util.misc.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class ProcessAddField extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    try (final var db = getEventregDb()) {
      db.open();

      final var form = getCalSuiteForm();
      if (form == null) {
        return;
      }

      checkFormOwner(form);

      final String fieldName = reqName();

      if (fieldName == null) {
        errorReturn("No field name");
        return;
      }

      final String fieldType = reqType();

      if (fieldType == null) {
        errorReturn("No field type");
        return;
      }

      if (!FieldDef.validTypes.contains(fieldType)) {
        errorReturn("Invalid field type: " + fieldType);
        return;
      }

      final FieldDef field = new FieldDef();

      String group = null;
      if (groupPresent()) {
        group = reqGroup();
      }

      field.setFormName(globals.getFormName());
      field.setOwner(form.getOwner());
      field.setName(fieldName);
      field.setType(fieldType);
      field.setLabel(reqLabel());
      field.setValue(reqValue());
      field.setGroup(reqDescription());
      field.setGroup(group);
      field.setRequired(reqRequired());
      field.setOrder(reqOrder());
      field.setDefaultValue(reqDefault());
      field.setMultivalued(reqMulti());
      field.setWidth(reqWidth());
      field.setHeight(reqHeight());

      final FormFields ff = new FormFields(form.getFields());

      if (fieldType.equals(FieldDef.fieldTypeOption)) {
        if (field.getGroup() == null) {
          errorReturn("No group set for option: " + fieldName);
          return;
        }

        if (ff.getField(field.getGroup()) == null) {
          errorReturn("No field " + field.getGroup() +
                              " for option: " + fieldName);
          return;
        }
      }

      form.addField(field);

      db.update(form);

      if (fieldType.equals(FieldDef.fieldTypeSelect) ||
              fieldType.equals(FieldDef.fieldTypeRadio)) {
        // Look for options
        final String options = reqOptions();

        if (options != null) {
          // Create a bunch of options
          if (!createOptions(form, field, options)) {
            errorReturn("Bad option for " + fieldName);
            return;
          }
        }
      }

      globals.setMessage("ok");
      setSessionAttr("form", form);
      setSessionAttr("fields", ff);
      forward("success");
    } catch (final EventregInvalidNameException eine) {
      errorReturn("Invalid name field");
    }
  }

  private boolean createOptions(final FormDef form,
                                final FieldDef field,
                                final String options) {
    final BufferedReader rdr = new BufferedReader(new StringReader(options));
    int i = 100;

    try {
      for (String line = rdr.readLine();
           line != null;
           line = rdr.readLine()) {
        final FieldDef opt = new FieldDef();

        final String[] valLabel = line.split("\\|");

        if (valLabel.length < 1) {
          return false;
        }

        final String value = Util.checkNull(valLabel[0]);

        if (value == null) {
          return false;
        }

        String label = null;

        if (valLabel.length == 2) {
          label = valLabel[1];
        } else if (valLabel.length > 2) {
          return false;
        }

        opt.setFormName(field.getFormName());
        opt.setOwner(field.getOwner());
        opt.setName(field.getName() + "-" + i);
        opt.setType(FieldDef.fieldTypeOption);
        opt.setGroup(field.getName());
        opt.setLabel(label);
        opt.setValue(value);
        //opt.setGroup(req.getDescription());
        //opt.setRequired(req.getRequired());
        opt.setOrder(i);
        opt.setDefaultValue(reqDefault());
        //opt.setMultivalued(req.getMulti());
        //opt.setWidth(req.getWidth());
        //opt.setHeight(req.getHeight());

        i += 100;
        form.addField(opt);
      }
      rdr.close();
    } catch (final IOException ioe) {
      throw new EventregException(ioe);
    }

    return true;
  }
}
