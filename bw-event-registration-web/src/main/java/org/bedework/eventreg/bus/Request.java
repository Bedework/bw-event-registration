/* ********************************************************************
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 */
package org.bedework.eventreg.bus;

import org.bedework.eventreg.common.EventregInvalidNameException;
import org.bedework.util.misc.Util;
import org.bedework.util.servlet.ReqUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author douglm
 *
 */
public class Request extends ReqUtil {
  /** Request parameter - admin token
   */
  public static final String reqparAdminToken = "atkn";

  /** Request parameter - calsuite
   */
  public static final String reqparCalsuite = "calsuite";

  /** Request parameter - comment
   */
  public static final String reqparComment = "comment";

  /** Request parameter - comment
   */
  public static final String reqparDescription = "description";

  /** Request parameter - href
   */
  public static final String reqparHref = "href";

  /** Request parameter - number of tickets
   */
  public static final String reqparNumtickets = "numtickets";

  /** Request parameter - ticket id
   */
  public static final String reqparRegId = "regid";

  /** Request parameter - add header to csv - default true
   */
  public static final String reqparCSVHeader = "csvheader";

  /** Request parameter - email address
   */
  public static final String reqparEmail = "email";

  /** Request parameter - filename
   */
  public static final String reqparFilename = "fn";

  /** Request parameter - lastmod
   */
  public static final String reqparLastmod = "lastmod";

  /* =====================================================
   *                form fields
   */

  public static final String reqparDefault = "default";

  /** Request parameter - form name
   */
  private static final String reqparFormName = "formName";

  private static final String reqparGroup = "group";

  private static final String reqparName = "name";

  public static final String reqparDisable = "disable";

  public static final String reqparHeight = "height";

  public static final String reqparLabel = "label";

  public static final String reqparMulti = "multi";

  public static final String reqparOrder = "order";

  public static final String reqparRequired = "req";

  public static final String reqparOptions = "options";

  public static final String reqparType = "type";

  public static final String reqparValue = "value";

  public static final String reqparWidth = "width";

  /**
   * @param request http request
   * @param response http response
   */
  public Request(final HttpServletRequest request,
                 final HttpServletResponse response) {
    super(request, response);
  }

  /**
   * @return number of tickets or -1 for no parameter
   */
  public int getTicketsRequested() {
    return getIntReqPar(reqparNumtickets, -1);
  }

  /**
   * @return admin token or null for no parameter
   */
  public String getAdminToken() {
    return getReqPar(reqparAdminToken);
  }

  /**
   * @param def default
   * @return filename or null for no parameter
   */
  public String getFilename(final String def) {
    return getReqPar(reqparFilename, def);
  }

  /**
   * @return ticket id or null for no parameter
   */
  public Long getRegistrationId() {
    return getLongReqPar(reqparRegId, -1);
  }

  /**
   * @return true if header required
   */
  public boolean getCsvHeader() {
    return getBooleanReqPar(reqparCSVHeader, true);
  }

  /**
   * @return comment or null for no parameter
   */
  public String getCalsuite() {
    return getReqPar(reqparCalsuite);
  }

  /**
   * @return comment or null for no parameter
   */
  public String getComment() {
    return getReqPar(reqparComment);
  }

  /**
   * @return lastmod or null for no parameter
   */
  public String getLastmod() {
    return getReqPar(reqparLastmod);
  }

  /**
   * @return email or null for no parameter
   */
  public String getEmail() {
    return getReqPar(reqparEmail);
  }

  /**
   * @return href or null for no parameter
   */
  public String getHref() {
    return getReqPar(reqparHref);
  }

  /* =====================================================
   *                form fields
   */

  /**
   * @return par or null for no parameter
   */
  public boolean getDefault() {
    return getBooleanReqPar(reqparDefault, false);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean formNamePresent() {
    return Util.checkNull(getReqPar(reqparFormName)) != null;
  }

  /**
   * @return par or null for no parameter
   */
  public String getFormName() {
    return validName(getReqPar(reqparFormName));
  }

  /**
   * @return par or null for no parameter
   */
  public String getName() {
    return validName(getReqPar(reqparName));
  }

  /**
   * @return par or null for no parameter
   */
  public boolean groupPresent() {
    return getReqPar(reqparGroup) != null;
  }

  /**
   * @return par or null for no parameter
   */
  public String getGroup() {
    return validName(getReqPar(reqparGroup));
  }

  /**
   * @return par or null for no parameter
   */
  public String getDescription() {
    return getReqPar(reqparDescription);
  }

  /**
   * @return par or null for no parameter
   */
  public String getHeight() {
    return getReqPar(reqparHeight);
  }

  /**
   * @return par or null for no parameter
   */
  public String getLabel() {
    return getReqPar(reqparLabel);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean getDisable() {
    return getBooleanReqPar(reqparDisable, false);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean getMulti() {
    return getBooleanReqPar(reqparMulti, false);
  }

  /**
   * @return par or null for no parameter
   */
  public int getOrder() {
    return getIntReqPar(reqparOrder, 0);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean getRequired() {
    return getBooleanReqPar(reqparRequired, false);
  }

  /**
   * @return par or null for no parameter
   */
  public String getOptions() {
    return getReqPar(reqparOptions);
  }

  /**
   * @return par or null for no parameter
   */
  public String getType() {
    return getReqPar(reqparType);
  }

  /**
   * @return par or null for no parameter
   */
  public String getValue() {
    return getReqPar(reqparValue);
  }

  /**
   * @return par or null for no parameter
   */
  public String getWidth() {
    return getReqPar(reqparWidth);
  }

  /* name validation. form, field and group names must all be
     valid json names.
   */
  public static String validName(final String name) {
    if ((name == null) || (name.isEmpty())) {
      throw new EventregInvalidNameException(name);
    }

    if (!Character.isLetter(name.charAt(0))) {
      throw new EventregInvalidNameException(name);
    }

    for (int i = 1; i < name.length(); i++) {
      final char ch = name.charAt(i);

      if (Character.isLetterOrDigit(ch)) {
        continue;
      }

      if ((ch == '-') || (ch == '_')) {
        continue;
      }

      throw new EventregInvalidNameException(name);
    }

    return name;
  }
}
