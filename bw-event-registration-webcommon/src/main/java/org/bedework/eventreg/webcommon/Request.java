package org.bedework.eventreg.webcommon;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.servlet.ReqUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

  /**
   * @return par or null for no parameter
   */
  public boolean getDisable() {
    return getBooleanReqPar("disable", false);
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
