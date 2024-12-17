package org.bedework.eventreg.webcommon;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.servlet.ReqUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Request extends ReqUtil implements Logged {
  /**
   * @param request the http request
   * @param response the response
   */
  public Request(final HttpServletRequest request,
                 final HttpServletResponse response) {
    super(request, response);
  }

  public WebGlobals getGlobals() {
    var globals = (WebGlobals)getSessionAttr(WebGlobals.webGlobalsAttrName);

    if (globals == null) {
      globals = new WebGlobals();
      setSessionAttr(WebGlobals.webGlobalsAttrName, globals);
    }

    return globals;
  }

  /* ============================================================
   *                   Logged methods
   * ============================================================ */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
