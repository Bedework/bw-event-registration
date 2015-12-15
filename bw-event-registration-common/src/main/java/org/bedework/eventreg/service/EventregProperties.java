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

import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.HibernateConfigI;
import org.bedework.util.jmx.MBeanInfo;

import java.util.List;

/** Properties for the Bedework event registration service
 *
 * @author douglm
 */
@ConfInfo(elementName = "eventreg-properties")
public interface EventregProperties extends HibernateConfigI {
  /* ========================================================================
   * Attributes
   * ======================================================================== */

  /** Set the token for event reg admins
   *
   * @param val the token for event reg admins
   */
  void setEventregAdminToken(final String val);

  /** Get the token for event reg admins
   *
   * @return token
   */
  @MBeanInfo("The token for event reg admins")
  String getEventregAdminToken();

  /**
   * @param val uri
   */
  void setTzsUri(String val);

  /**
   * @return tzs uri
   */
  @MBeanInfo("Timezones service URI")
  String getTzsUri();

  /** Location of wsdl
   *
   * @param val uri
   */
  void setWsdlUri(String val);

  /**
   * @return Location of wsdl
   */
  @MBeanInfo("Location of wsdl")
  String getWsdlUri();

  /**
   *
   * @param val the token for bedework webservice calls
   */
  void setBwId(final String val);

  /** Get the token for bedework webservice calls
   *
   * @return token
   */
  @MBeanInfo("token for bedework webservice calls")
  String getBwId();

  /**
   *
   * @param val the token for bedework webservice calls
   */
  void setBwToken(final String val);

  /** Get the token for bedework webservice calls
   *
   * @return token
   */
  @MBeanInfo("token for bedework webservice calls")
  String getBwToken();

  /**
   *
   * @param val the url for bedework webservice calls
   */
  void setBwUrl(final String val);

  /** Get the url for bedework webservice calls
   *
   * @return url
   */
  @MBeanInfo("URL for bedework webservice calls")
  String getBwUrl();

  /**
   *
   * @param val batch size for registration ids
   */
  void setRegidBatchSize(final int val);

  /**
   *
   * @return batch size for registration ids
   */
  @MBeanInfo("batch size for registration ids")
  int getRegidBatchSize();

  /* ========================================================================
   * Schema
   * ======================================================================== */

  /**
   *
   * @param val Output file name - full path
   */
  void setSchemaOutFile(String val);

  /**
   * @return Output file name - full path
   */
  @MBeanInfo("Full path of schema output file")
  String getSchemaOutFile();

  /* ========================================================================
   * Dump/restore
   * ======================================================================== */

  /**
   *
   * @param val XML data input file name - full path. Used for data restore
   */
  void setDataIn(String val);

  /**
   * @return XML data input file name - full path
   */
  @MBeanInfo("XML data input file name - full path")
  String getDataIn();

  /**
   *
   * @param val XML data output directory name - full path. Used for data restore
   */
  void setDataOut(String val);

  /**
   * @return XML data output directory name - full path
   */
  @MBeanInfo("XML data output file name - full path")
  String getDataOut();

  /** XML data output file prefix - for data dump
   *
   * @param val the prefix
   */
  void setDataOutPrefix(String val);

  /**
   * @return XML data output file prefix - for data dump
   */
  @MBeanInfo("XML data output file prefix - for data dump")
  String getDataOutPrefix();

  /** Create an email address as current user + "@" + this
   *
   * @param val Create an email address as current user + "@" + this
   */
  void setDefaultEmailDomain(String val);

  /**
   * @return Create an email address as current user + "@" + this
   */
  @MBeanInfo("Create an email address as current user + \"@\" + this")
  String getDefaultEmailDomain();

  /* sysevents properties
   */

  /**
   *
   * @param val The action queue name
   */
  void setActionQueueName(String val);

  /**
   * @return The action queue name - e.g. eventregAction
   */
  @MBeanInfo("The action queue name - e.g. eventregAction")
  String getActionQueueName();

  /**
   *
   * @param val The delay queue name
   */
  void setActionDelayQueueName(String val);

  /**
   * @return The delay queue name - e.g. eventregActionDelay
   */
  @MBeanInfo("The delay queue name - e.g. eventregActionDelay")
  String getActionDelayQueueName();

  /**
   *
   * @param val how long we delay failed actions
   */
  void setDelayMillis(final int val);

  /**
   *
   * @return how long we delay failed actions
   */
  @MBeanInfo("how long we delay failed actions")
  int getDelayMillis();

  /**
   *
   * @param val how often we retry failed actions
   */
  void setRetries(final int val);

  /**
   *
   * @return how often we retry failed actions
   */
  @MBeanInfo("how often we retry failed actions")
  int getRetries();

  /**
   *
   * @param val the list of properties
   */
  void setSyseventsProperties(final List<String> val);

  /**
   *
   * @return String val
   */
  @ConfInfo(collectionElementName = "syseventsProperty" ,
          elementType = "java.lang.String")
  List<String> getSyseventsProperties();

  /** Add a sysevents property
   *
   * @param name of property
   * @param val of property
   */
  void addSyseventsProperty(final String name,
                            final String val);

  /** Get a sysevents property
   *
   * @param name of property
   * @return value or null
   */
  @ConfInfo(dontSave = true)
  String getSyseventsProperty(final String name);

  /** Remove a sysevents property
   *
   * @param name of property
   */
  void removeSyseventsProperty(final String name);

  /** Set a sysevents property
   *
   * @param name of property
   * @param val of property
   */
  @ConfInfo(dontSave = true)
  void setSyseventsProperty(final String name,
                            final String val);
}
