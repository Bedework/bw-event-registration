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

import org.bedework.eventreg.service.Configuration;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.ServletBase;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/** This servlet handles the categories requests and respondes.
 *
 * @author Mike Douglass   bedework.com
 * @version 1.0
 */
public abstract class EvregServlet extends ServletBase {
  protected void initMethodBase(final MethodBase mb,
                                final ConfBase conf,
                                final ServletContext context,
                                final boolean dumpContent,
                                final String methodName) throws ServletException {
    final var cfg = (Configuration)conf;
    cfg.loadConfig();
    final EvregMethodBase emb = (EvregMethodBase)mb;

    try {
      emb.init(cfg.getConfig(),
               context,
               dumpContent,
               methodName,
               appInfo);
    } catch (final Throwable t) {
      throw new ServletException(t);
    }
  }

  /* -------------------------------------------------------------
   *                         JMX support
   */

  private static final Configuration conf = new Configuration("eventreg", "eventreg");

  protected ConfBase<?> getConfigurator() {
    return conf;
  }
}
