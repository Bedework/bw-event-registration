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

import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.FormFields;
import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author douglm
 *
 */
public class ProcessOutputCSV extends EvregAdminMethodHelper {
  private final String fixedHeader =
          "event," +
                  "date," +
                  "time," +
                  "location," +
                  "registrationId," +
                  "userid," +
                  "ticketsRequested," +
                  "ticketsAllocated," +
                  "type," +
                  "comment," +
                  "created," +
                  "lastmod";

  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    if (!requireHref()) {
      return;
    }

    final var href = globals.getHref();
    final Event ev = getConnector().flush(href)
                                   .getEvent(href);

    try (final var db = getEventregDb()) {
      db.open();

      resp.setHeader(
              "Content-Disposition",
              "Attachment; Filename=\"" +
                      getRequest().getReqPar("fn",
                                             "eventreg.csv") + "\"");
      resp.setContentType("application/vnd.ms-excel; charset=UTF-8");

      final var form = getCalSuiteForm();
      try (final var wtr = resp.getWriter()) {
        if (reqCsvHeader()) {
          wtr.println(getHeader(getCalSuiteForm()));
        }

        for (final var reg: db.getByEvent(href)) {
          reg.setEvent(ev);
          wtr.println(outRegistration(reg, form));
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public String getHeader(final FormDef form) {
    final StringBuilder sb = new StringBuilder(fixedHeader);

    if (form == null) {
      return sb.toString();
    }

    final FormFields ff = new FormFields(form.getFields());

    for (final FieldDef fd: ff) {
      sb.append(",").append(fd.getName());
    }

    return sb.toString();
  }

  public String outRegistration(final Registration reg,
                                final FormDef form) {
    final List<Object> flds = new ArrayList<>();

    final StringBuilder out = new StringBuilder();

    try (final CSVPrinter csv = new CSVPrinter(out, CSVFormat.EXCEL)) {
      flds.add(reg.getEvSummary());
      flds.add(reg.getEvDate());
      flds.add(reg.getEvTime());
      flds.add(reg.getEvLocation());
      flds.add(reg.getRegistrationId());
      flds.add(reg.getAuthid());
      flds.add(reg.getTicketsRequested());
      flds.add(reg.getNumTickets());
      flds.add(reg.getType());
      flds.add(reg.getComment());
      flds.add(reg.getCreated());
      flds.add(reg.getLastmod());

      if (form == null) {
        csv.printRecord(flds.toArray());
        csv.flush();
        csv.close();
        return out.toString();
      }

      final FormFields ff = new FormFields(form.getFields());

      try {
        final var vals = reg.restoreFormValues();

        for (final FieldDef fd: ff) {
          flds.add(Objects.requireNonNullElse(
                  vals.get(fd.getName()), ""));
        }
      } catch (final Throwable t) {
        out.append("Exception restoring form values");
      }

      csv.printRecord(flds.toArray());
      csv.flush();
    } catch (final Throwable t) {
      throw new RuntimeException(t);
    }

    return out.toString();
  }
}
