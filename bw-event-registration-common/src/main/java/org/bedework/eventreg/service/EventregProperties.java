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
   * @param val
   */
  void setTzsUri(String val);

  /**
   * @return tzs uri
   */
  @MBeanInfo("Timezones service URI")
  String getTzsUri();

  /** Location of wsdl
   *
   * @param val
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

  /* ========================================================================
   * Schema
   * ======================================================================== */

  /** Output file name - full path
   *
   * @param val
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

  /** XML data input file name - full path. Used for data restore
   *
   * @param val
   */
  void setDataIn(String val);

  /**
   * @return XML data input file name - full path
   */
  @MBeanInfo("XML data input file name - full path")
  String getDataIn();

  /** XML data output directory name - full path. Used for data restore
   *
   * @param val
   */
  void setDataOut(String val);

  /**
   * @return XML data output directory name - full path
   */
  @MBeanInfo("XML data output file name - full path")
  String getDataOut();

  /** XML data output file prefix - for data dump
   *
   * @param val
   */
  void setDataOutPrefix(String val);

  /**
   * @return XML data output file prefix - for data dump
   */
  @MBeanInfo("XML data output file prefix - for data dump")
  String getDataOutPrefix();
}
