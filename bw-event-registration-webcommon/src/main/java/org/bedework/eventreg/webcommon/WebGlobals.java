/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.eventreg.webcommon;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.servlet.HttpServletUtils;

import java.io.Serializable;
import java.util.Locale;

/**
 * User: mike Date: 6/26/24 Time: 23:04
 */
public class WebGlobals implements Logged, Serializable {
  // Stored in session
  public final static String webGlobalsAttrName = "globals";

  /**
   * The current authenticated user. May be null
   */
  protected String currentUser;

  protected String calsuite;

  protected Locale currentLocale;

  private String formName;

  private String href;

  private String message;

  public void reset(final Request req) {
    if (getCurrentUser() == null) {
      currentUser = HttpServletUtils.remoteUser(req.getRequest());
    } // Otherwise we check it later in checklogout.
  }

  /**
   * Checks if there has been a change in the authenticated user for the current request.
   * <p>
   *   Must be called after reset(...)
   *
   * @param req the current HTTP request containing user information
   * @return true if a user change is detected; false otherwise
   */
  public boolean userChanged(final Request req) {
    if (!getCurrentUser().equals(
            HttpServletUtils.remoteUser(req.getRequest()))) {
      req.getRequest().getSession().invalidate();
      return true;
    }
    return false;
  }

  public void logout(final Request req) {
    req.getRequest().getSession().invalidate();
  }

  /**
   *
   * @return current authenticated user or null
   */
  public String getCurrentUser() {
    return currentUser;
  }

  public void setCalsuite(final String val) {
    calsuite = val;
  }

  /**
   *
   * @return current calendar suite name
   */
  public String getCalsuite() {
    return calsuite;
  }

  public Locale getCurrentLocale() {
    if (currentLocale == null) {
      return Locale.getDefault();
    }
    return currentLocale;
  }

  public void setFormName(final String val) {
    formName = val;
  }

  public String getFormName() {
    return formName;
  }

  public void setHref(final String val) {
    href = val;
  }

  public String getHref() {
    return href;
  }

  public void setMessage(final String msg) {
    message = msg;
  }

  public String getMessage() {
    return message;
  }

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
