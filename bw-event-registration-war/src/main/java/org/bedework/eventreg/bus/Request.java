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

  /** Request parameter - comment
   */
  public static final String reqparComment = "comment";

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
   * @throws Throwable
   */
  public int getTicketsRequested() throws Throwable {
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
   * @throws Throwable
   */
  public Long getRegistrationId() throws Throwable {
    return getLongReqPar(reqparRegId, -1);
  }

  /**
   * @return true if header required
   * @throws Throwable
   */
  public boolean getCsvHeader() throws Throwable {
    return getBooleanReqPar(reqparCSVHeader, true);
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
}
