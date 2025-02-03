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
package org.bedework.eventreg.web;

import org.bedework.eventreg.service.Configuration;
import org.bedework.eventreg.webcommon.EvregServlet;
import org.bedework.util.jmx.ConfBase;

import static org.bedework.util.servlet.MethodBase.MethodInfo;

/** This servlet handles the categories reuests and respondes.
 *
 * @author Mike Douglass   bedework.com
 * @version 1.0
 */
public class EvregUserServlet extends EvregServlet {
  @Override
  protected void addMethods() {
    addMethod("POST",
              new MethodInfo(EvregUserMethod.class, true));
    methods.put("GET",
                new MethodInfo(EvregUserMethod.class, false));
    /*
    methods.put("ACL", new MethodInfo(AclMethod.class, false));
    methods.put("COPY", new MethodInfo(CopyMethod.class, false));
    methods.put("HEAD", new MethodInfo(HeadMethod.class, false));
    methods.put("OPTIONS", new MethodInfo(OptionsMethod.class, false));
    methods.put("PROPFIND", new MethodInfo(PropFindMethod.class, false));

    methods.put("DELETE", new MethodInfo(DeleteMethod.class, true));
    methods.put("MKCOL", new MethodInfo(MkcolMethod.class, true));
    methods.put("MOVE", new MethodInfo(MoveMethod.class, true));
    methods.put("POST", new MethodInfo(PostMethod.class, true));
    methods.put("PROPPATCH", new MethodInfo(PropPatchMethod.class, true));
    methods.put("PUT", new MethodInfo(PutMethod.class, true));
    */

    //methods.put("LOCK", new MethodInfo(LockMethod.class, true));
    //methods.put("UNLOCK", new MethodInfo(UnlockMethod.class, true));
  }

  /* -------------------------------------------------------------
   *                         JMX support
   */

  private static final Configuration conf = new Configuration("eventreg", "eventreg");

  @Override
  protected ConfBase<?> getConfigurator() {
    return conf;
  }
}
