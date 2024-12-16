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
package org.bedework.eventreg.webadmin;

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.timezones.Timezones;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Base class for all webdav servlet methods.
 */
public abstract class EvregAdminMethodBase extends MethodBase {
  private ObjectMapper objectMapper;
  private EventregProperties config;
  private EventregDb db;
  private BwConnector connector;

  private final static XcalUtil.TzGetter tzs = Timezones::getTz;

  @Override
  public void init() {
    
  }

  @Override
  public boolean beforeMethod(final HttpServletRequest req,
                              final HttpServletResponse resp) {
    if (!super.beforeMethod(req, resp)) {
      return false;
    }

    final var token = rutil.getReqPar("atkn");
    if ((token == null) ||
            !token.equals(config.getEventregAdminToken())) {
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }

    return true;
  }

  /** Called at each request
   *
   * @param config configuration
   * @param dumpContent true to wrap and dump content for debugging
   */
  public void init(final EventregProperties config,
                   final boolean dumpContent) {
    this.config = config;
    this.dumpContent = dumpContent;

    init();
  }
  
  public ObjectMapper getMapper() {
    if (objectMapper != null) {
      return objectMapper;
    }

    objectMapper = new ObjectMapper();
    return objectMapper;
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

