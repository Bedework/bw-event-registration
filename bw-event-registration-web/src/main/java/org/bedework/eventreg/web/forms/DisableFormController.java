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

import java.util.List;

/** Needs formName and calSuite to fetch
 *
 * @author douglm
 *
 */
public class DisableFormController extends AuthAbstractController {
  @Override
  public ModelAndView doRequest() {
    if (getSessMan().getCurrentCalsuite() == null) {
      return errorReturn("No calsuite");
    }

    final String formName = req.getFormName();

    final FormDef form = getSessMan().getFormDef(formName);

    if (form == null) {
      return errorReturn("Form " + formName + " does not exist");
    }

    form.setDisabled(req.getDisable());

    getSessMan().updateFormDef(form);

    final List<FormDef> forms = getSessMan().getFormDefs();

    return objModel(getForwardSuccess(),
                    "forms", forms);
  }
}
