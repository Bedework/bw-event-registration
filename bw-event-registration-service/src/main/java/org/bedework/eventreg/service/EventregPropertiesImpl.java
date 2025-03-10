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

import org.bedework.eventreg.common.EventregProperties;
import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.OrmConfigBase;

import java.util.List;

/**
 * @author douglm
 *
 */
@ConfInfo(elementName = "eventreg-properties",
          type = "org.bedework.eventreg.service.EventregProperties")
public class EventregPropertiesImpl extends OrmConfigBase<EventregPropertiesImpl>
        implements EventregProperties {
  /* =========================================================
   * Attributes
   * ========================================================= */

  private String eventregAdminToken;

  private String eventregWSUrl;

  private String adminUsers;

  private String tzsUri;

  private String wsdlUri;

  private String bwId;

  private String bwToken;

  private String bwUrl;

  private int regidBatchSize;

  private String actionQueueName;

  private String actionDelayQueueName;

  private int delayMillis;

  private int retries;

  private List<String> syseventsProperties;

  /* =========================================================
   * Dump/restore
   * ========================================================= */

  private String schemaOutFile;

  private String dataIn;

  private String dataOut;

  private String dataOutPrefix;

  private String defaultEmailDomain;

  /* =========================================================
   * Attributes
   * ========================================================= */

  @Override
  public void setEventregAdminToken(final String val) {
    eventregAdminToken = val;
  }

  @Override
  public String getEventregAdminToken() {
    return eventregAdminToken;
  }

  @Override
  public void setEventregWSUrl(final String val) {
    eventregWSUrl = val;
  }

  @Override
  public String getEventregWSUrl() {
    return eventregWSUrl;
  }

  @Override
  public void setAdminUsers(final String val) {
    adminUsers = val;
  }

  @Override
  public String getAdminUsers() {
    return adminUsers;
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

  @Override
  public void setBwId(final String val) {
    bwId = val;
  }

  @Override
  public String getBwId() {
    return bwId;
  }

  @Override
  public void setBwToken(final String val) {
    bwToken = val;
  }

  @Override
  public String getBwToken() {
    return bwToken;
  }

  @Override
  public void setBwUrl(final String val) {
    bwUrl = val;
  }

  @Override
  public String getBwUrl() {
    return bwUrl;
  }

  @Override
  public void setRegidBatchSize(final int val) {
    regidBatchSize = val;
  }

  @Override
  public int getRegidBatchSize() {
    return regidBatchSize;
  }

  /* =========================================================
   * Dump/restore
   * ========================================================= */

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

  @Override
  public void setDefaultEmailDomain(final String val) {
    defaultEmailDomain = val;
  }

  @Override
  public String getDefaultEmailDomain() {
    return defaultEmailDomain;
  }

  @Override
  public void setActionQueueName(final String val) {
    actionQueueName = val;
  }

  @Override
  public String getActionQueueName() {
    return actionQueueName;
  }

  @Override
  public void setActionDelayQueueName(final String val) {
    actionDelayQueueName = val;
  }

  @Override
  public String getActionDelayQueueName() {
    return actionDelayQueueName;
  }

  @Override
  public void setDelayMillis(final int val) {
    delayMillis = val;
  }

  @Override
  public int getDelayMillis() {
    return delayMillis;
  }

  @Override
  public void setRetries(final int val) {
    retries = val;
  }

  @Override
  public int getRetries() {
    return retries;
  }

  @Override
  public void setSyseventsProperties(final List<String> val) {
    syseventsProperties = val;
  }

  @Override
  @ConfInfo(collectionElementName = "syseventsProperty" ,
          elementType = "java.lang.String")
  public List<String> getSyseventsProperties() {
    return syseventsProperties;
  }

  @Override
  public void addSyseventsProperty(final String name,
                                   final String val) {
    setSyseventsProperties(addListProperty(getSyseventsProperties(),
                                           name, val));
  }

  @Override
  public String getSyseventsProperty(final String name) {
    return getProperty(getSyseventsProperties(), name);
  }

  @Override
  public void removeSyseventsProperty(final String name) {
    removeProperty(getSyseventsProperties(), name);
  }

  @Override
  public void setSyseventsProperty(final String name,
                                   final String val) {
    setSyseventsProperties(setListProperty(getSyseventsProperties(),
                                           name, val));
  }
}

