package org.bedework.eventreg.ws.gethelpers;

import org.bedework.eventreg.webcommon.EvregMethodHelper;
import org.bedework.eventreg.ws.SelectFormsInfo;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProcessSelectForms extends EvregMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    final String calsuite = getReqUtil().getReqPar("calsuite");
    if (calsuite == null) {
      sendJsonError(resp, "CalSuite not supplied");
      return;
    }

    try (final var db = getEventregDb()) {
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

      outputJson(resp, null, null, finfo);
    }
  }
}
