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
package org.bedework.eventreg.ws.gethelpers;

import org.bedework.eventreg.requests.EventChangeRequest;
import org.bedework.eventreg.service.EventregSenderHandler;
import org.bedework.eventreg.service.SvcRequestSender;
import org.bedework.eventreg.webcommon.EvregMethodHelper;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Incoming notification of an event change
 *
 * @author douglm
 *
 */
public class ProcessEventChange extends EvregMethodHelper {
  private static EventregSenderHandler sender;
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    if (!requireHref()) {
      warn("No href supplied for processing event change");
      return;
    }

    final var href = globals.getHref();

    if (debug()) {
      debug("Event change: " + href);
    }

    final var ecr = new EventChangeRequest(href);

    getSender().addRequest(ecr);
  }

  private EventregSenderHandler getSender() {
    if (sender == null) {
      synchronized (this) {
        if (sender == null) {
          sender = new SvcRequestSender(emb.getConfig());
        }
      }
    }
    return sender;
  }
}
