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

import org.bedework.eventreg.EventregProperties;
import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.HibernateConfigBase;

/**
 * @author douglm
 *
 */
@ConfInfo(elementName = "eventreg-properties",
          type = "org.bedework.eventreg.service.EventregProperties")
public class EventregPropertiesImpl extends HibernateConfigBase<EventregPropertiesImpl>
        implements EventregProperties {
  /* ========================================================================
   * Attributes
   * ======================================================================== */

  private String eventregAdminToken;

  private String tzsUri;

  private String wsdlUri;

  /* ========================================================================
   * Dump/restore
   * ======================================================================== */

  private String schemaOutFile;

  private String dataIn;

  private String dataOut;

  private String dataOutPrefix;

  /* ========================================================================
   * Attributes
   * ======================================================================== */

  @Override
  public void setEventregAdminToken(final String val) {
    eventregAdminToken = val;
  }

  @Override
  public String getEventregAdminToken() {
    return eventregAdminToken;
  }

  @Override
  public void setTzsUri(final String val) {
    tzsUri = val;
  }

  @Override
  public String getTzsUri() {
    return tzsUri;
  }

  @Override
  public void setWsdlUri(final String val) {
    wsdlUri = val;
  }

  @Override
  public String getWsdlUri() {
    return wsdlUri;
  }

  /* ========================================================================
   * Dump/restore
   * ======================================================================== */

  @Override
  public void setSchemaOutFile(final String val) {
    schemaOutFile = val;
  }

  @Override
  public String getSchemaOutFile() {
    return schemaOutFile;
  }

  @Override
  public void setDataIn(final String val) {
    dataIn = val;
  }

  @Override
  public String getDataIn() {
    return dataIn;
  }

  @Override
  public void setDataOut(final String val) {
    dataOut = val;
  }

  @Override
  public String getDataOut() {
    return dataOut;
  }

  @Override
  public void setDataOutPrefix(final String val) {
    dataOutPrefix = val;
  }

  @Override
  public String getDataOutPrefix() {
    return dataOutPrefix;
  }
}

