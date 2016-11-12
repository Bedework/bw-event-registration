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

package org.bedework.eventreg.db;



/**
 * Change log entry
 *
 */
public class RegId extends DbItem<RegId> {
  private Long nextRegistrationId;

  /**
   *
   */
  public RegId() {
  }

  /**
   * @param val
   */
  public void setNextRegistrationId(final Long val) {
    nextRegistrationId = val;
  }

  /**
   * @return next registrationId
   */
  public Long getNextRegistrationId() {
    return nextRegistrationId;
  }

  /* ====================================================================
   *                   Object methods
   * The following are required for a db object.
   * ==================================================================== */

  @Override
  public int compareTo(final RegId that) {
    // This is wrong
    return getNextRegistrationId().compareTo(that.getNextRegistrationId());
  }

  @Override
  public int hashCode() {
    return getNextRegistrationId().hashCode();
  }
}


