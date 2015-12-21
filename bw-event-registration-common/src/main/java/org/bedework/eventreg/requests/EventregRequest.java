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

import org.bedework.util.jms.events.SysEvent;
import org.bedework.util.misc.ToString;

/** Requests coming in to the Bedework event registration service or
 * generated internally as a result of changes.
 * These are handled asynchronously by the service module.
 *
 *
 * @author douglm
 */
public class EventregRequest extends SysEvent {
  private static final long serialVersionUID = 1L;

  /* millisecs time to retry */
  private long waitUntil;

  private int retries;
  private boolean discard;

  /**
   * @param code the system event code
   */
  public EventregRequest(final String code) {
    super(code);
  }

  public int getRetries() {
    return retries;
  }

  public void incRetries() {
    retries++;
  }

  public void setWaitUntil(final long val) {
    waitUntil = val;
  }

  public long getWaitUntil() {
    return waitUntil;
  }

  public boolean getDiscard() {
    return discard;
  }

  public void discard() {
    discard = true;
  }

  public void toStringSegment(final ToString ts) {
    ts.append("retries", getRetries());
    ts.append("discard", getDiscard());
  }

  public String toString() {
    final ToString ts = new ToString(this);

    toStringSegment(ts);

    return ts.toString();
  }
}
