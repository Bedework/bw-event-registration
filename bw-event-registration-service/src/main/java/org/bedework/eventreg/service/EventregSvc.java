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

import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.database.hibernate.HibConfig;
import org.bedework.database.hibernate.SchemaThread;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.jmx.InfoLines;
import org.bedework.util.jmx.MBeanInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author douglm
 *
 */
public class EventregSvc extends ConfBase<EventregPropertiesImpl>
        implements EventregSvcMBean {
  /* Name of the directory holding the config data */
  private static final String confDirName = "eventreg";


  /* Be safe - default to false */
  private boolean export;

  /* =======================================================
   * Dump/restore
   * ======================================================= */

  private class SchemaBuilder extends SchemaThread {

    SchemaBuilder(final String outFile, 
                  final boolean export) {
      super(outFile, export, new HibConfig(getConfig(),
                                           EventregSvc.class.getClassLoader()));
      setContextClassLoader(EventregSvc.class.getClassLoader());
    }

    @Override
    public void completed(final String status) {
      if (status.equals(SchemaThread.statusDone)) {
        EventregSvc.this.setStatus(ConfBase.statusDone);
      } else {
        EventregSvc.this.setStatus(ConfBase.statusFailed);
      }
      setExport(false);
      info("Schema build completed with status " + status);
    }
  }
  
  private SchemaBuilder buildSchema;

  private final static String nm = "eventreg";

  private EventregRequestHandler hdlr;

  /**
   */
  @SuppressWarnings("unused")
  public EventregSvc() {
    super(getServiceName(nm),
          confDirName,
          nm);
  }

  /**
   * @param name of service
   * @return object name value for the mbean with this name
   */
  public static String getServiceName(final String name) {
    return "org.bedework.eventreg:service=" + name;
  }

  @Override
  public String loadConfig() {
    return loadConfig(EventregPropertiesImpl.class);
  }

  public void setEventregRequestHandler(final EventregRequestHandler val) {
    hdlr = val;
  }

  /* =======================================================
   * System properties
   * ======================================================= */

  @Override
  public void setEventregAdminToken(final String val) {
    getConfig().setEventregAdminToken(val);
  }

  @Override
  public String getEventregAdminToken() {
    return getConfig().getEventregAdminToken();
  }

  @Override
  public void setEventregWSUrl(final String val) {
    getConfig().setEventregWSUrl(val);
  }

  @Override
  public String getEventregWSUrl() {
    return getConfig().getEventregWSUrl();
  }

  @Override
  public void setAdminUsers(final String val) {
    getConfig().setAdminUsers(val);
  }

  @Override
  public String getAdminUsers() {
    return getConfig().getAdminUsers();
  }

  @Override
  public void setTzsUri(final String val) {
    getConfig().setTzsUri(val);
  }

  @Override
  public String getTzsUri() {
    return getConfig().getTzsUri();
  }

  @Override
  public void setWsdlUri(final String val) {
    getConfig().setWsdlUri(val);
  }

  @Override
  public String getWsdlUri() {
    return getConfig().getWsdlUri();
  }

  @Override
  public void setBwId(final String val) {
    getConfig().setBwId(val);
  }

  @Override
  public String getBwId() {
    return getConfig().getBwId();
  }

  @Override
  public void setBwToken(final String val) {
    getConfig().setBwToken(val);
  }

  @Override
  public String getBwToken() {
    return getConfig().getBwToken();
  }

  @Override
  public void setBwUrl(final String val) {
    getConfig().setBwUrl(val);
  }

  @Override
  public String getBwUrl() {
    return getConfig().getBwUrl();
  }

  @Override
  public void setRegidBatchSize(final int val) {
    getConfig().setRegidBatchSize(val);
  }

  @Override
  public int getRegidBatchSize() {
    return getConfig().getRegidBatchSize();
  }

  /* =======================================================
   * Hibernate properties
   * ======================================================= */

  @Override
  public void setHibernateProperties(final List<String> val) {
    getConfig().setHibernateProperties(val);
  }

  @Override
  public List<String> getHibernateProperties() {
    return getConfig().getHibernateProperties();
  }

  @Override
  public void setHibernateDialect(final String value) {
    getConfig().setHibernateDialect(value);
  }

  @Override
  public String getHibernateDialect() {
    return getConfig().getHibernateDialect();
  }

  @Override
  public void removeHibernateProperty(final String name) {
    getConfig().removeHibernateProperty(name);
  }

  @Override
  public void addHibernateProperty(final String name,
                                   final String value) {
    getConfig().addHibernateProperty(name, value);
  }

  @Override
  public String getHibernateProperty(final String name) {
    return getConfig().getHibernateProperty(name);
  }

  @Override
  public void setHibernateProperty(final String name,
                                   final String value) {
    getConfig().setHibernateProperty(name, value);
  }

  /* =======================================================
   * Dump/restore
   * ======================================================= */

  @Override
  public void setSchemaOutFile(final String val) {
    getConfig().setSchemaOutFile(val);
  }

  @Override
  public String getSchemaOutFile() {
    return getConfig().getSchemaOutFile();
  }

  @Override
  public void setDataIn(final String val) {
    getConfig().setDataIn(val);
  }

  @Override
  public String getDataIn() {
    return getConfig().getDataIn();
  }

  @Override
  public void setDataOut(final String val) {
    getConfig().setDataOut(val);
  }

  @Override
  public String getDataOut() {
    return getConfig().getDataOut();
  }

  @Override
  public void setDataOutPrefix(final String val) {
    getConfig().setDataOutPrefix(val);
  }

  @Override
  public String getDataOutPrefix() {
    return getConfig().getDataOutPrefix();
  }

  @Override
  public void setDefaultEmailDomain(final String val) {
    getConfig().setDefaultEmailDomain(val);
  }

  @Override
  public String getDefaultEmailDomain() {
    return getConfig().getDefaultEmailDomain();
  }

  @Override
  public void setActionQueueName(final String val) {
    getConfig().setActionQueueName(val);
  }

  @Override
  public String getActionQueueName() {
    return getConfig().getActionQueueName();
  }

  @Override
  public void setActionDelayQueueName(final String val) {
    getConfig().setActionDelayQueueName(val);
  }

  @Override
  public String getActionDelayQueueName() {
    return getConfig().getActionDelayQueueName();
  }

  @Override
  public void setDelayMillis(final int val) {
    getConfig().setDelayMillis(val);
  }

  @Override
  public int getDelayMillis() {
    return getConfig().getDelayMillis();
  }

  @Override
  public void setRetries(final int val) {
    getConfig().setRetries(val);
  }

  @Override
  public int getRetries() {
    return getConfig().getRetries();
  }

  @Override
  public void setSyseventsProperties(final List<String> val) {
    getConfig().setSyseventsProperties(val);
  }

  @Override
  public List<String> getSyseventsProperties() {
    return getConfig().getSyseventsProperties();
  }

  @Override
  public void addSyseventsProperty(final String name,
                                   final String val) {
    getConfig().addSyseventsProperty(name, val);
  }

  @Override
  public String getSyseventsProperty(final String name) {
    return getConfig().getSyseventsProperty(name);
  }

  @Override
  public void removeSyseventsProperty(final String name) {
    getConfig().removeSyseventsProperty(name);
  }

  @Override
  public void setSyseventsProperty(final String name,
                                   final String val) {
    getConfig().setSyseventsProperty(name, val);
  }

  /* =======================================================
   * Mbean attributes
   * ======================================================= */

  @Override
  public void setExport(final boolean val) {
    export = val;
  }

  @Override
  @MBeanInfo("Export (write) schema to database?")
  public boolean getExport() {
    return export;
  }

  /* =======================================================
   * Operations
   * ======================================================= */

  @Override
  public String generateAdminToken() {
    setEventregAdminToken(UUID.randomUUID().toString());

    return "OK";
  }

  @Override
  public boolean queueRequest(final EventregRequest request) {
    if (!isRunning()) {
      return false;
    }

    try {
      hdlr.addRequest(request);
    } catch (final Throwable t) {
      error(t);

      return false;
    }

    return true;
  }

  @Override
  public String schema() {
    try {
      buildSchema = new SchemaBuilder(
              getSchemaOutFile(),
              getExport());

      setStatus(statusRunning);

      buildSchema.start();

      return "OK";
    } catch (final Throwable t) {
      error(t);

      return "Exception: " + t.getLocalizedMessage();
    }
  }

  @Override
  public synchronized List<String> schemaStatus() {
    if (buildSchema == null) {
      final InfoLines infoLines = new InfoLines();

      infoLines.addLn("Schema build has not been started");

      return infoLines;
    }

    return buildSchema.infoLines;
  }

  @Override
  public String listHibernateProperties() {
    final StringBuilder res = new StringBuilder();

    final List<String> ps = getConfig().getHibernateProperties();

    for (final String p: ps) {
      res.append(p);
      res.append("\n");
    }

    return res.toString();
  }

  @Override
  public String displayHibernateProperty(final String name) {
    final String val = getConfig().getHibernateProperty(name);

    if (val != null) {
      return val;
    }

    return "Not found";
  }

  @Override
  public synchronized List<String> restoreData() {
    final List<String> infoLines = new ArrayList<>();

    try {
      /*
      if (!disableIndexer()) {
        infoLines.add("***********************************\n");
        infoLines.add("********* Unable to disable indexer\n");
        infoLines.add("***********************************\n");
      }

      long startTime = System.currentTimeMillis();

      Restore r = new Restore(getConfiguration());

      String[] args = new String[] {"-appname",
                                    appname
      };

      r.getConfigProperties(new Args(args));

      r.setFilename(getDataIn());

      r.setNoIndexes(true);

      r.open();

      r.doRestore();

      r.close();

      r.stats(infoLines);

      long millis = System.currentTimeMillis() - startTime;
      long seconds = millis / 1000;
      long minutes = seconds / 60;
      seconds -= (minutes * 60);

      infoLines.add("Elapsed time: " + minutes + ":" +
                    Restore.twoDigits(seconds) + "\n");

      infoLines.add("Restore complete" + "\n");
      */
      infoLines.add("************************Restore unimplemented *************************" + "\n");
    } catch (final Throwable t) {
      error(t);
      infoLines.add("Exception - check logs: " + t.getMessage() + "\n");
      /*
    } finally {
      try {
        if (!reindex()) {
          infoLines.add("***********************************");
          infoLines.add("********* Unable to disable indexer");
          infoLines.add("***********************************");
        }
      } catch (Throwable t) {
        error(t);
        infoLines.add("Exception - check logs: " + t.getMessage() + "\n");
      }
        */
    }

    return infoLines;
  }

  @Override
  public List<String> restoreStatus() {
    final List<String> res = new ArrayList<>();

    res.add("************************Restore unimplemented *************************" + "\n");

    return res;
  }

  @Override
  public List<String> dumpData() {
    final List<String> infoLines = new ArrayList<>();

    try {
      /*
      long startTime = System.currentTimeMillis();

      Dump d = new Dump(getConfiguration());

      String[] args = new String[] {"-appname",
                                    appname
      };

      d.getConfigProperties(args);

      StringBuilder fname = new StringBuilder(getDataOut());
      if (!getDataOut().endsWith("/")) {
        fname.append("/");
      }

      fname.append(getDataOutPrefix());

      /* append "yyyyMMddTHHmmss" * /
      fname.append(DateTimeUtil.isoDateTime());
      fname.append(".xml");

      d.setFilename(fname.toString());

      d.open();

      d.doDump();

      d.close();

      d.stats(infoLines);

      long millis = System.currentTimeMillis() - startTime;
      long seconds = millis / 1000;
      long minutes = seconds / 60;
      seconds -= (minutes * 60);

      infoLines.add("Elapsed time: " + minutes + ":" +
                    Restore.twoDigits(seconds) + "\n");

      infoLines.add("Dump complete" + "\n");
      */
      infoLines.add("************************Dump unimplemented *************************" + "\n");
    } catch (final Throwable t) {
      error(t);
      infoLines.add("Exception - check logs: " + t.getMessage() + "\n");
    }

    return infoLines;
  }

  @Override
  public List<String> dumpStatus() {
    final List<String> res = new ArrayList<>();

    res.add("************************Dump unimplemented *************************" + "\n");

    return res;
  }

  @Override
  public boolean isRunning() {
    return hdlr.isRunning();
  }

  @Override
  public synchronized void start() {
    hdlr.start();
  }

  @Override
  public synchronized void stop() {
    hdlr.stop();
  }
}
