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
package org.bedework.eventreg.service;

import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.util.jmx.MBeanInfo;

/** Does the work of processing eventreg system messages
 *
 * @author douglm
 *
 */
public interface EventregRequestHandler {
  /** Lifecycle
   *
   */
  @MBeanInfo("Start the service")
  void start();

  /** Lifecycle
   *
   */
  @MBeanInfo("Stop the service")
  void stop();

  /** Lifecycle
   *
   * @return true if service running
   */
  @MBeanInfo("Show if service is running")
  boolean isRunning();

  /** Listen for messages and process them.
   *
   * Returns at shutdown.
   */
  void listen();

  /**
   *
   * @param val a request for processing
   */
  void addRequest(final EventregRequest val);

  /**
   * Shut down the process
   */
  void close();
}
