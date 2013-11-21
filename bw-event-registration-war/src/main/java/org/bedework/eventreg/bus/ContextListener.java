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

import org.bedework.eventreg.service.EventregSvc;
import org.bedework.eventreg.service.EventregSvcMBean;
import org.bedework.util.jmx.ConfBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author douglm
 *
 */
public class ContextListener implements ServletContextListener {
  protected final Log logger = LogFactory.getLog(getClass());

  static class Configurator extends ConfBase {
    EventregSvc sysInfo;

    public Configurator() {
      super("org.bedework.eventreg:service=EventRegContext");
    }

    @Override
    public String loadConfig() {
      return null;
    }

    @Override
    public void start() {
      try {
        getManagementContext().start();

        sysInfo = new EventregSvc();
        register(new ObjectName(sysInfo.getServiceName()),
                 sysInfo);
        sysInfo.loadConfig();
      } catch (Throwable t){
        t.printStackTrace();
      }
    }

    @Override
    public void stop() {
      try {
        getManagementContext().stop();
      } catch (Throwable t){
        t.printStackTrace();
      }
    }
  }

  private static Configurator conf = new Configurator();

  static {
    // Initialise now so it's visible after deployment
    conf.start();
  }

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    conf.stop();
  }

  public static EventregSvcMBean getSysInfo() throws Throwable {
    return conf.sysInfo;
  }
}
