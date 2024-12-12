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
package org.bedework.eventreg.web;

import org.bedework.eventreg.bus.CSVOutputter;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.FormDef;

import org.springframework.web.servlet.ModelAndView;

import java.util.TreeSet;

/**
 * @author douglm
 *
 */
public class OutputCSVController extends AdminAuthAbstractController {
  @Override
  public ModelAndView doRequest() {
    if (getSessMan().getCurrentCalsuite() == null) {
      return errorReturn("No calsuite");
    }

    final String formName = getSessMan().getCurrentFormName();

    final FormDef form = getSessMan().getFormDef(formName);

    final TreeSet<Registration> regs = new TreeSet<>();

    for (final var reg:
            getSessMan().getRegistrationsByHref(req.getHref())) {
      reg.setEvent(getSessMan().retrieveEvent(reg));

      regs.add(reg);
    }

    final CSVOutputter csv = new CSVOutputter(getSessMan().getCurrEvent(),
                                              form, regs);

    req.getResponse().setHeader("Content-Disposition",
                       "Attachment; Filename=\"" +
                           req.getFilename("eventreg.csv") + "\"");
    //response.setContentType("application/vnd.ms-excel; charset=UTF-8");

    return objModel(getForwardSuccess(), "csv", csv);
  }
}
