package org.bedework.eventreg.ws.getHelpers;

import org.bedework.eventreg.ws.EvregwsMethodBase;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.MethodHelper;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessListForms implements MethodHelper {
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp,
                      final MethodBase mb)
          throws ServletException {
    final String calsuite = mb.getReqUtil().getReqPar("calsuite");
    final var emb = (EvregwsMethodBase)mb;
    if (calsuite == null) {
      mb.sendJsonError(resp, "CalSuite not supplied");
      return;
    }

    try (final var db = emb.getEventregDb()) {
      db.open();

      final var forms = db.getCalSuiteForms(calsuite);

      mb.outputJson(resp, null, null, forms);
    }
  }
}
