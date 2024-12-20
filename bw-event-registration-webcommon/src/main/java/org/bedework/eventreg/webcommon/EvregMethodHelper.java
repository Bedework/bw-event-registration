package org.bedework.eventreg.webcommon;

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.FormDef;
import org.bedework.util.servlet.MethodHelper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

public abstract class EvregMethodHelper extends MethodHelper {

  /* =====================================================
   *                form fields
   */

  /** Request parameter - comment
   */
  public static final String reqparComment = "comment";

  public static final String reqparDefault = "default";

  /** Request parameter - comment
   */
  public static final String reqparDescription = "description";

  public static final String reqparDisable = "disable";

  /** Request parameter - form name
   */
  private static final String reqparFormName = "formName";

  private static final String reqparGroup = "group";

  public static final String reqparHeight = "height";

  public static final String reqparLabel = "label";

  public static final String reqparMulti = "multi";

  private static final String reqparName = "name";

  public static final String reqparOptions = "options";

  public static final String reqparOrder = "order";

  public static final String reqparRequired = "req";

  public static final String reqparType = "type";

  public static final String reqparValue = "value";

  public static final String reqparWidth = "width";

  private EvregMethodBase emb;
  protected WebGlobals globals;

  public abstract void evProcess(List<String> resourceUri,
                                 HttpServletRequest req,
                                 HttpServletResponse resp);
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    emb = (EvregMethodBase)super.getMethodBase();
    globals = emb.getWebGlobals();
    evProcess(resourceUri, req, resp);
  }

  protected EvregMethodBase getMethodBase() {
    return emb;
  }

  protected Request getRequest() {
    return emb.getRequest();
  }

  public boolean requireCalsuite() {
    if (globals.getCalsuite() == null) {
      errorReturn("Bad resource url - no calsuite specified");
      return false;
    }
    return true;
  }

  public boolean requireFormName() {
    if (globals.getFormName() == null) {
      errorReturn("Bad resource url - no form namee specified");
      return false;
    }
    return true;
  }

  /**
   * @return par or null for no parameter
   */
  public String reqName() {
    return emb.validName(getReqUtil().getReqPar("name"));
  }

  /**
   * @return par or null for no parameter
   */
  public boolean groupPresent() {
    return getReqUtil().getReqPar(reqparGroup) != null;
  }

  /**
   * @return par or null for no parameter
   */
  public String reqGroup() {
    return emb.validName(getReqUtil().getReqPar(reqparGroup));
  }

  /**
   * @return comment or null for no parameter
   */
  public String reqComment() {
    return getReqUtil().getReqPar(reqparComment);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqDescription() {
    return getReqUtil().getReqPar(reqparDescription);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqType() {
    return getReqUtil().getReqPar("type");
  }

  /**
   * @return par or null for no parameter
   */
  public String reqValue() {
    return getReqUtil().getReqPar(reqparValue);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqWidth() {
    return getReqUtil().getReqPar(reqparWidth);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqHeight() {
    return getReqUtil().getReqPar(reqparHeight);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqLabel() {
    return getReqUtil().getReqPar(reqparLabel);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean reqMulti() {
    return getReqUtil().getBooleanReqPar(reqparMulti, false);
  }

  /**
   * @return par or null for no parameter
   */
  public int reqOrder() {
    return getReqUtil().getIntReqPar(reqparOrder, 0);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean reqRequired() {
    return getReqUtil().getBooleanReqPar(reqparRequired, false);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean reqDefault() {
    return getReqUtil().getBooleanReqPar(reqparDefault, false);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqOptions() {
    return getReqUtil().getReqPar(reqparOptions);
  }

  protected EventregDb getEventregDb() {
    return emb.getEventregDb();
  }

  public FormDef getCalSuiteForm() {
    final var calsuite = globals.getCalsuite();
    final var formName = globals.getFormName();

    final var form =
            emb.getEventregDb().getCalSuiteForm(formName,
                                                calsuite);

    if (form == null) {
      errorReturn(
              format("Form %s not found for calsuite %s",
                     formName, calsuite));
      return null;
    }

    return form;
  }

  protected void checkFormOwner(final FormDef form) {
    if (!form.getOwner().equals(globals.getCalsuite())) {
      throw new EventregException("Owner not current calsuite");
    }
  }

  protected BwConnector getConnector() {
    return emb.getConnector();
  }

  protected void errorReturn(final Throwable t) {
    emb.errorReturn(t.getLocalizedMessage());
  }

  protected void errorReturn(final String msg) {
    emb.errorReturn("/docs/error.jsp", msg);
  }

  protected void errorReturn(final String forward,
                                     final String msg) {
    emb.errorReturn(forward, msg);
  }
}
