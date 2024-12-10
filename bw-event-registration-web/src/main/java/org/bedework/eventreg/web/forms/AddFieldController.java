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
package org.bedework.eventreg.web.forms;

import org.bedework.eventreg.bus.FormFields;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregInvalidNameException;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.web.AuthAbstractController;
import org.bedework.util.misc.Util;

import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author douglm
 *
 */
public class AddFieldController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() {
    try {
      if (getSessMan().getCurrentCalsuite() == null) {
        return errorReturn("No calsuite");
      }

      final String formName = req.getFormName();

      final FormDef form = getSessMan().getFormDef(formName);

      if (form == null) {
        return errorReturn("No current form");
      }

      final String fieldName = req.getName();

      if (fieldName == null) {
        return errorReturn("No field name");
      }

      final String fieldType = req.getType();

      if (fieldType == null) {
        return errorReturn("No field type");
      }

      if (!FieldDef.validTypes.contains(fieldType)) {
        return errorReturn("Invalid field type: " + fieldType);
      }

      final FieldDef field = new FieldDef();

      String group = null;
      if (req.groupPresent()) {
        group = req.getGroup();
      }

      field.setFormName(formName);
      field.setOwner(form.getOwner());
      field.setName(fieldName);
      field.setType(fieldType);
      field.setLabel(req.getLabel());
      field.setValue(req.getValue());
      field.setGroup(req.getDescription());
      field.setGroup(group);
      field.setRequired(req.getRequired());
      field.setOrder(req.getOrder());
      field.setDefaultValue(req.getDefault());
      field.setMultivalued(req.getMulti());
      field.setWidth(req.getWidth());
      field.setHeight(req.getHeight());

      final FormFields ff = new FormFields(form.getFields());

      if (fieldType.equals(FieldDef.fieldTypeOption)) {
        if (field.getGroup() == null) {
          return errorReturn("No group set for option: " + fieldName);
        }

        if (ff.getField(field.getGroup()) == null) {
          return errorReturn("No field " + field.getGroup() +
                                     " for option: " + fieldName);
        }
      }

      form.addField(field);

      getSessMan().updateFormDef(form);

      if (fieldType.equals(FieldDef.fieldTypeSelect) ||
              fieldType.equals(FieldDef.fieldTypeRadio)) {
        // Look for options
        final String options = req.getOptions();

        if (options != null) {
          // Create a bunch of options
          if (!createOptions(form, field, options)) {
            return errorReturn("Bad option for " + fieldName);
          }
        }
      }

      getSessMan().setCurrentFormName(formName);

      getSessMan().setMessage("ok");
      return objModel(getForwardSuccess(), "form", form,
                      "fields", ff);
    } catch (final EventregInvalidNameException eine) {
      return errorReturn("Invalid name field");
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
          if (debug()) {
            // ....
          }
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
        opt.setDefaultValue(req.getDefault());
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
