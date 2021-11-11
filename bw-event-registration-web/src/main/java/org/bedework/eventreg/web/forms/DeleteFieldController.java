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
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.web.AuthAbstractController;

import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

/**
 * @author douglm
 *
 */
public class DeleteFieldController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() throws Throwable {
    if (sessMan.getCurrentCalsuite() == null) {
      return errorReturn("No calsuite");
    }

    final String formName = req.getFormName();

    final FormDef form = sessMan.getFormDef(formName);

    if (form == null) {
      return errorReturn("No current form");
    }

    final String fieldName = req.getName();

    if (fieldName == null) {
      return errorReturn("No field name");
    }

    FieldDef fld = null;
    final Set<FieldDef> flds = form.getFields();

    for (final FieldDef f: flds) {
      if (f.getName().equals(fieldName)) {
        fld = f;
        break;
      }
    }

    if (fld == null) {
      return errorReturn("Field not found " + fieldName);
    }

    flds.remove(fld);
    sessMan.updateFormDef(form);

    sessMan.setCurrentFormName(formName);

    sessMan.setMessage("ok");
    return objModel(getForwardSuccess(), "form", form,
                    "fields", new FormFields(flds));
  }
}
