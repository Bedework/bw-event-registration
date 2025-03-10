/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.eventreg.webcommon;

import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.common.RegistrationInfo;
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

  protected Locale currentLocale;

  protected String calsuite;

  private String currentEmail;

  private Event currentEvent;

  private boolean deadlinePassed;

  private Registration reg;

  private RegistrationInfo regInfo;

  private boolean registrationFull;

  private String formName;

  private String href;

  private String message;

  private boolean waiting;

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
    if (getCurrentUser() == null) {
      return false; // Not a change
    }

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

  public Locale getCurrentLocale() {
    if (currentLocale == null) {
      return Locale.getDefault();
    }
    return currentLocale;
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

  public void setCurrentEmail(final String val) {
    currentEmail = val;
  }

  public String getCurrentEmail() {
    return currentEmail;
  }

  public void setCurrentEvent(final Event val) {
    currentEvent = val;
  }

  public Event getCurrentEvent() {
    return currentEvent;
  }

  /**
   * @return true if deadline passed
   */
  public boolean getDeadlinePassed() {
    return deadlinePassed;
  }

  /**
   * @param val true if passed deadline
   */
  public void setDeadlinePassed(final boolean val) {
    deadlinePassed = val;
  }

  public void setWaiting(final boolean val) {
    waiting = val;
  }

  /**
   * @return true if current user is on waiting list for current event
   */
  public boolean getIsWaiting() {
    return waiting;
  }

  public void setRegistration(final Registration val) {
    reg = val;
  }

  public Registration getRegistration() {
    return reg;
  }

  public boolean isRegistered() {
    return reg != null;
  }

  public void setRegInfo(final RegistrationInfo val) {
    regInfo = val;
  }

  public RegistrationInfo getRegInfo() {
    return regInfo;
  }

  /**
   * @return true if current registration is full
   */
  public boolean getRegistrationFull() {
    return registrationFull;
  }

  /**
   * @param val true if current registration is full
   */
  public void setRegistrationFull(final boolean val) {
    registrationFull = val;
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
