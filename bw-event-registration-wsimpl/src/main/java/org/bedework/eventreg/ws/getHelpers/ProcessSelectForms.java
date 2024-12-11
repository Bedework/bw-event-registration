package org.bedework.eventreg.ws.getHelpers;

import org.bedework.eventreg.ws.EvregwsMethodBase;
import org.bedework.eventreg.ws.SelectFormsInfo;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.MethodHelper;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessSelectForms implements MethodHelper {
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp,
                      final MethodBase mb)
          throws ServletException {
    final var emb = (EvregwsMethodBase)mb;
    final String calsuite = mb.getReqUtil().getReqPar("calsuite");
    if (calsuite == null) {
      mb.sendJsonError(resp, "CalSuite not supplied");
      return;
    }

    try (final var db = emb.getEventregDb()) {
      db.open();

      final var forms = db.getCalSuiteForms(calsuite);
      final var finfo = new SelectFormsInfo();

      for (final var form: forms) {
        final String status;
        if (form.getDisabled()) {
          status = "disabled";
        } else if (form.getCommitted()) {
          status = "committed";
        } else {
          status = "unpublished";
        }
        finfo.addForm(form.getFormName(), status);
      }

      mb.outputJson(resp, null, null, finfo);
    }
  }
}
