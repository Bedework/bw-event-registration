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

import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.jms.JmsNotificationsHandlerImpl;
import org.bedework.util.jms.NotificationException;
import org.bedework.util.jms.NotificationsHandler;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

/** Does the work of processing a request
 *
 * @author douglm
 *
 */
public class SvcRequestSender
        implements Logged, EventregSenderHandler {
  private final EventregProperties props;

  private final NotificationsHandler sender;

  public SvcRequestSender(final EventregProperties props) {
    this.props = props;

    try {
      sender = new JmsNotificationsHandlerImpl(
              props.getActionQueueName(),
              ConfigBase.toProperties(
                      props.getSyseventsProperties()));
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  @Override
  public void addRequest(final EventregRequest val) {
    try {
      sender.post(val);
    } catch (final NotificationException e) {
      throw new EventregException(e);
    }
  }

  public EventregProperties getSysInfo() {
    return props;
  }

  /* ====================================================================
   *                   Logged methods
   * ==================================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
