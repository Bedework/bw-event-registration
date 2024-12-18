package org.bedework.eventreg.webadmin.gethelpers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessListForms extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    final var calsuite = globals.getCalsuite();

    if (calsuite == null) {
      errorReturn("CalSuite not supplied");
      return;
    }

    try (final var db = getEventregDb()) {
      db.open();

      final var forms = db.getCalSuiteForms(calsuite);
      setSessionAttr("forms", forms);
      forward("/docs/forms/listForms.jsp");
    }
  }
}
