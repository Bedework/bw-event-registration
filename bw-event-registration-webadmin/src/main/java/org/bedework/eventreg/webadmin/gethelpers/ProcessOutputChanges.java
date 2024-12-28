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

import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class ProcessOutputChanges extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    try (final var db = getEventregDb()) {
      db.open();

      final List<Change> cs = db.getChanges(reqLastmod());

      final var fn =
              getRequest().getReqPar("fn",
                                     "eventregChanges.csv");

      resp.setHeader("Content-Disposition",
                     "Attachment; Filename=\"" +
                             fn + "\"");
      resp.setContentType("application/vnd.ms-excel; charset=UTF-8");

      try (final var wtr = resp.getWriter()) {
        if (reqCsvHeader()) {
          wtr.println("registrationId,account,lastmod," +
                              "type,name,value");
        }

        for (final var c: db.getChanges(reqLastmod())) {
          wtr.println(outChange(c));
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
    forward("success");
  }

  public String outChange(final Change chg) {
    final List<Object> flds = new ArrayList<>();

    final StringBuilder out = new StringBuilder();

    try (final CSVPrinter csv = new CSVPrinter(out, CSVFormat.EXCEL)) {
      flds.add(chg.getRegistrationId());
      flds.add(chg.getAuthid());
      flds.add(chg.getLastmod());
      flds.add(chg.getType());
      flds.add(chg.getName());
      flds.add(chg.getValue());

      csv.printRecord(flds.toArray());
      csv.flush();
    } catch (final Throwable t) {
      throw new RuntimeException(t);
    }

    return out.toString();
  }
}
