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
package org.bedework.eventreg.requests;

import org.bedework.eventreg.db.Registration;
import org.bedework.util.misc.ToString;

import java.io.Serial;

/** Requests coming in to the Bedework event registration service.
 * These are the ones that need to be handled asynchronously, for
 * example, notifications of event changes.
 *
 *
 * @author douglm
 */
public class RegistrationAction extends EventregRequest {
  @Serial
  private static final long serialVersionUID = 1L;

  public static final String syseventEventregRegistration =
          "EventregActionRegistration";

  private final Registration reg;

  public RegistrationAction(final Registration reg) {
    super(syseventEventregRegistration);
    this.reg = reg;
  }

  public Registration getReg() {
    return reg;
  }

  @Override
  public void toStringSegment(final ToString ts) {
    super.toStringSegment(ts);

    ts.append("registration", getReg());
  }
}
