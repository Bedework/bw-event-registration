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

import org.bedework.eventreg.db.Event;
import org.bedework.eventreg.db.FieldDef;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.db.Registration;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Handle CSV output of data
 *
 * @author douglm
 */
public class CSVOutputter
        implements Logged, Iterator<String>, Iterable<String> {
  private final Event ev;
  private final Set<Registration> regs;
  private final FormDef form;

  private final Iterator<Registration> regit;

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


  public CSVOutputter(final Event ev,
                      final FormDef form,
                      final Set<Registration> regs) {
    this.ev = ev;
    this.regs = regs;
    this.form = form;

    regit = regs.iterator();
  }

  public String getHeader() {
    final StringBuilder sb = new StringBuilder(fixedHeader);

    if (form == null) {
      return sb.toString();
    }

    final FormFields ff = new FormFields(form.getFields());

    for (final FieldDef fd: ff) {
      sb.append(",");
      sb.append(fd.getName());
    }

    return sb.toString();
  }

  @Override
  public boolean hasNext() {
    return regit.hasNext();
  }

  @Override
  public String next() {
    if (!regit.hasNext()) {
      return null;
    }

    /*
    ><c:forEach var="reg" items="${regs}" varStatus="loopStatus"><%--
            </c:forEach>
    */
    final List<Object> flds = new ArrayList<>();

    final Registration reg = regit.next();

    final StringBuilder out = new StringBuilder();

    try {
      final CSVPrinter csv = new CSVPrinter(out, CSVFormat.EXCEL);
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
      csv.close();
    } catch (final Throwable t) {
      return "Exception " + t.getLocalizedMessage();
    }

    return out.toString();
  }

  @Override
  public void remove() {

  }

  @Override
  public Iterator<String> iterator() {
    return this;
  }

  /* ========================================================
   *                   Logged methods
   * ======================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
