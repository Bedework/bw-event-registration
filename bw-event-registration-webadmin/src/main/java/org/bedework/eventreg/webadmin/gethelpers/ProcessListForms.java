package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProcessListForms extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    try (final var db = getEventregDb()) {
      db.open();

      setSessionAttr("forms", getCalSuiteForms());
      forward("success");
    }
  }
}
