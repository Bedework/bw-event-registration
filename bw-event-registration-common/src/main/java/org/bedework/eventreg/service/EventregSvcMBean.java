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
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.util.jmx.ConfBaseMBean;
import org.bedework.util.jmx.MBeanInfo;

import java.util.List;

/** Run the Bedework synch engine service
 *
 * @author douglm
 */
public interface EventregSvcMBean extends ConfBaseMBean,
        EventregProperties {
  /** Export schema to database?
   *
   * @param val
   */
  public void setExport(boolean val);

  /**
   * @return true for export schema
   */
  @MBeanInfo("Export (write) schema to database?")
  public boolean getExport();

  /* =========================================================
   * Operations
   * ======================================================== */

  /** Create or dump new schema. If export and drop set will try to drop tables.
   * Export and create will create a schema in the db and export, drop, create
   * will drop tables, and try to create  anew schema.
   *
   * The export, create and drop flags will all be reset to false after this,
   * whatever the result. This avoids accidental damage to the db.
   *
   * @return Completion message
   */
  @MBeanInfo("Start build of the database schema. Set export flag to write to db.")
  String schema();

  /** Returns status of the schema build.
   *
   * @return Completion messages
   */
  @MBeanInfo("Status of the database schema build.")
  List<String> schemaStatus();

  /** List the orm properties
   *
   * @return properties
   */
  @MBeanInfo("List the orm properties")
  String listOrmProperties();

  /** Display the named property
   *
   * @param name of property
   * @return value
   */
  @MBeanInfo("Display the named orm property")
  String displayOrmProperty(@MBeanInfo("name") final String name);

  /** Restores the data from the DataIn path. Will not restore if there appears
   * to be any data already in the db.
   *
   * @return Completion messages and stats
   */
  @MBeanInfo("Restores the data from the DataIn path")
  List<String> restoreData();

  /** Returns status of the restore.
   *
   * @return Completion messages and stats
   */
  @MBeanInfo("Show state of current restore")
  List<String> restoreStatus();

  /** Dumps the data to a file in the DataOut directory.
   *
   * @return Completion messages and stats
   */
  @MBeanInfo("Dumps the data to a file in the DataOut directory")
  List<String> dumpData();

  /** Returns status of the dump.
   *
   * @return Completion messages and stats
   */
  @MBeanInfo("Show status of current data dump")
  List<String> dumpStatus();

  /** Generate an admin token.
   *
   * @return Completion message
   */
  @MBeanInfo("Generate an admin token")
  String generateAdminToken();

  /**
   *
   * @param request to be queued
   * @return false if service not accepting requests
   */
  @MBeanInfo("handle an incoming request")
  boolean queueRequest(EventregRequest request);

  void setEventregRequestHandler(final EventregRequestHandler val);
}
