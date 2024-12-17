package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.webadmin.EvregAdminMethodBase;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.MethodHelper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessListForms implements MethodHelper {
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp,
                      final MethodBase mb) {
    final var emb = (EvregAdminMethodBase)mb;

    final var calsuite = emb.getWebGlobals().getCalsuite();

    if (calsuite == null) {
      mb.sendJsonError(resp, "CalSuite not supplied");
      return;
    }

    try (final var db = emb.getEventregDb()) {
      db.open();

      final var forms = db.getCalSuiteForms(calsuite);
      mb.getReqUtil().setSessionAttr("forms", forms);
      mb.forward("/docs/forms/listForms.jsp");
    }
  }
}
