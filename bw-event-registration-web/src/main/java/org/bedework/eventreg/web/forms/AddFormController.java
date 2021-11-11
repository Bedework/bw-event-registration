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

import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.web.AuthAbstractController;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author douglm
 *
 */
public class AddFormController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() throws Throwable {
    if (sessMan.getCurrentCalsuite() == null) {
      return errorReturn("No calsuite");
    }

    final String formName = req.getFormName();

    FormDef form = sessMan.getFormDef(formName);

    if (form != null) {
      return errorReturn("Form " + formName + " already exists");
    }

    form = new FormDef();

    form.setFormName(formName);
    form.setOwner(sessMan.getCurrentCalsuite());
    form.setComment(req.getComment());
    form.setDisabled(false);

    form.setTimestamps();

    sessMan.addFormDef(form);

    return objModel(getForwardSuccess(), "form", form);
  }
}
