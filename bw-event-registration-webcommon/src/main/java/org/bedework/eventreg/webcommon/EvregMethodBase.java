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
package org.bedework.eventreg.webcommon;

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregInvalidNameException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.misc.Util;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.ReqUtil;
import org.bedework.util.servlet.config.AppInfo;
import org.bedework.util.timezones.Timezones;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Base class for all event registration servlet methods.
   Subclasses should provide a static block registering
   the helper classes.
   e.g.
   <pre>
   static {
     registerHelper("listForms",
                    ProcessListForms.class);
     registerHelper("reginfo",
                    ProcessRegInfo.class);
     registerHelper("selectForms",
                    ProcessSelectForms.class);
   }
   </pre>
 */
public abstract class EvregMethodBase extends MethodBase {
  private ObjectMapper objectMapper;
  private EventregProperties config;
  private EventregDb db;
  private BwConnector connector;
  private WebGlobals webGlobals;

  private final static XcalUtil.TzGetter tzs = Timezones::getTz;

  @Override
  public boolean beforeMethod(final HttpServletRequest req,
                              final HttpServletResponse resp) {
    if (!super.beforeMethod(req, resp)) {
      return false;
    }

    webGlobals = (WebGlobals)rutil.getSessionAttr(WebGlobals.webGlobalsAttrName);
    if (webGlobals == null) {
      webGlobals = newWebGlobals();
      rutil.setSessionAttr(WebGlobals.webGlobalsAttrName,
                           webGlobals);
    }

    webGlobals.reset(getRequest());

    // commonly needed
    final var calsuite = rutil.getReqPar("calsuite");
    if (calsuite != null) {
      webGlobals.setCalsuite(calsuite);
    }

    final var formName = rutil.getReqPar("formName");
    if (formName != null) {
      webGlobals.setFormName(validName(formName));
    }

    webGlobals.setHref(rutil.getReqPar("href"));

    return true;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public void doMethod(final HttpServletRequest req,
                       final HttpServletResponse resp) throws ServletException {
    try {
      final List<String> resourceUri = getResourceUri(req);

      if (Util.isEmpty(resourceUri)) {
        throw new ServletException("Bad resource url - no path specified");
      }

      final String resName = resourceUri.get(0);
      final var helper = getMethodHelper(resName);
      if (helper == null) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }

      helper.execute(resourceUri, req, resp, this);
    } catch (final ServletException se) {
      throw se;
    } catch(final Throwable t) {
      throw new ServletException(t);
    }
  }

  /** Override to create a subclassed object
   *
   * @return new web globals object
   */
  public WebGlobals newWebGlobals() {
    return new WebGlobals();
  }

  public WebGlobals getWebGlobals() {
    return webGlobals;
  }

  @Override
  public ReqUtil newReqUtil(final HttpServletRequest req,
                            final HttpServletResponse resp) {
    return new Request(req, resp);
  }

  public Request getRequest() {
    return (Request)rutil;
  }

  /** Called at each request
   *
   * @param config configuration
   * @param dumpContent true to wrap and dump content for debugging
   */
  public void init(final EventregProperties config,
                   final ServletContext context,
                   final boolean dumpContent,
                   final String methodName,
                   final AppInfo appInfo) {
    init(context, dumpContent, methodName, appInfo);
    this.config = config;
  }

  public EventregProperties getConfig() {
    return config;
  }
  
  public ObjectMapper getMapper() {
    if (objectMapper != null) {
      return objectMapper;
    }

    objectMapper = new ObjectMapper();
    return objectMapper;
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

  public EventregDb getEventregDb() {
    if (db != null) {
      return db;
    }

    synchronized (this) {
      if (db != null) {
        return db;
      }
      db = new EventregDb();
      db.setSysInfo(config);
    }

    return db;
  }

  public BwConnector getConnector() {
    if (connector != null) {
      return connector;
    }

    synchronized (this) {
      if (connector != null) {
        return connector;
      }

      try {
        Timezones.initTimezones(config.getTzsUri());

        connector = new BwConnector(config.getWsdlUri(), tzs);
      } catch (final Throwable t) {
        throw new EventregException(t);
      }
    }

    return connector;
  }
}

