package org.bedework.eventreg.ws.gethelpers;

import org.bedework.eventreg.webcommon.EvregMethodHelper;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProcessRegInfo extends EvregMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    final String href = getReqUtil().getReqPar("href");

    if (href == null) {
      sendJsonError(resp, "Href not supplied");
      return;
    }

    try (final var db = getEventregDb()) {
      db.open();

      final var rinfo =
              db.getRegistrationInfo(
                      getConnector().getEvent(href),
                      href);

      outputJson(resp, null, null, rinfo);
    }
  }
}
