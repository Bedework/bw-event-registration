package org.bedework.eventreg.ws.getHelpers;

import org.bedework.eventreg.ws.EvregwsMethodBase;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.MethodHelper;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessRegInfo implements MethodHelper {
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp,
                      final MethodBase mb)
          throws ServletException {
    final String href = mb.getReqUtil().getReqPar("href");
    final var emb = (EvregwsMethodBase)mb;
    if (href == null) {
      mb.sendJsonError(resp, "Href not supplied");
      return;
    }

    try (final var db = emb.getEventregDb()) {
      db.open();

      final var rinfo = db.getRegistrationInfo(null, href);

      mb.outputJson(resp, null, null, rinfo);
    }
  }
}
