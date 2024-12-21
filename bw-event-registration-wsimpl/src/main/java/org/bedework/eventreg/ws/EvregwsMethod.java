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
package org.bedework.eventreg.ws;

import org.bedework.eventreg.webcommon.EvregMethodBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Base class for all webdav servlet methods.
 */
public class EvregwsMethod extends EvregMethodBase {
  @Override
  public boolean beforeMethod(final HttpServletRequest req,
                              final HttpServletResponse resp) {
    if (!super.beforeMethod(req, resp)) {
      return false;
    }

    final var token = rutil.getReqPar("atkn");
    if ((token == null) ||
            !token.equals(getConfig().getEventregAdminToken())) {
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }

    return true;
  }
}
