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

import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.jmx.InfoLines;
import org.bedework.util.jmx.MBeanInfo;
import org.bedework.util.misc.AbstractProcessorThread;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author douglm
 *
 */
public class EventregSvc extends ConfBase<EventregPropertiesImpl>
        implements EventregSvcMBean {
  /* Name of the property holding the location of the config data */
  public static final String confuriPname = "org.bedework.eventreg.confuri";

  public static final String statusDone = "Done";
  public static final String statusFailed = "Failed";
  public static final String statusRunning = "Running";
  public static final String statusStopped = "Stopped";

  private static String status;

  /* Be safe - default to false */
  private boolean export;

  /* ==============================================================
   * Dump/restore
   * ============================================================== */

  private Configuration hibCfg;

  private class SchemaThread extends Thread {
    InfoLines infoLines = new InfoLines();

    SchemaThread() {
      super("BuildSchema");
    }

    @Override
    public void run() {
      try {
        infoLines.addLn("Started export of schema");

        final long startTime = System.currentTimeMillis();

        final SchemaExport se = new SchemaExport(getHibConfiguration());

//      if (getDelimiter() != null) {
//        se.setDelimiter(getDelimiter());
//      }

        se.setFormat(true);       // getFormat());
        se.setHaltOnError(false); // getHaltOnError());
        se.setOutputFile(getSchemaOutFile());
        /* There appears to be a bug in the hibernate code. Everybody initialises
        this to /import.sql. Set to null causes an NPE
        Make sure it refers to a non-existant file */
        //se.setImportFile("not-a-file.sql");

        se.execute(false, // script - causes write to System.out if true
                   getExport(),
                   false,   // drop
                   true);   //   getCreate());

        final long millis = System.currentTimeMillis() - startTime;
        long seconds = millis / 1000;
        final long minutes = seconds / 60;
        seconds -= (minutes * 60);

        infoLines.addLn("Elapsed time: " + minutes + ":" +
                                twoDigits(seconds));
      } catch (final Throwable t) {
        error(t);
        infoLines.exceptionMsg(t);
      } finally {
        infoLines.addLn("Schema build completed");
        setExport(false);
      }
    }
  }

  private final SchemaThread buildSchema = new SchemaThread();

  private final static String nm = "eventreg";

  private EventregRequestHandler hdlr;

  /**
   * This process handles queued up requests from the calendar
   * system.
   */
  private class Processor extends AbstractProcessorThread {
    private final BlockingDeque<EventregRequest> requests =
            new LinkedBlockingDeque<>();
    // Capacity could be specified for the queue

    /**
     * @param name for the thread
     */
    public Processor(final String name) throws Throwable {
      super(name);
    }

    public void addRequest(final EventregRequest val) throws Throwable {
      requests.put(val);
    }

    @Override
    public void runInit() {
    }

    @Override
    public void runProcess() throws Throwable {
      while (running) {
        final EventregRequest req = requests.take();

        if (debug) {
          debug("handling request: " + req);
        }

        hdlr.handle(req);
      }
    }

    @Override
    public void close() {
      while (!requests.isEmpty()) {
        final EventregRequest req = requests.remove();

        try {
          hdlr.handle(req);
        } catch (final Throwable t) {
          error(t);
        }
      }
    }
  }

  AbstractProcessorThread processor;

  /**
   */
  @SuppressWarnings("unused")
  public EventregSvc() {
    super(getServiceName(nm));

    setConfigName(nm);

    setConfigPname(confuriPname);
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

  AbstractProcessorThread getProcessor() throws Throwable {
    return new Processor("Eventreg");
  }

  public void setEventregRequestHandler(final EventregRequestHandler val) {
    hdlr = val;
  }

  /* ========================================================================
   * System properties
   * ======================================================================== */

  @Override
  public void setEventregAdminToken(final String val) {
    getConfig().setEventregAdminToken(val);
  }

  @Override
  public String getEventregAdminToken() {
    return getConfig().getEventregAdminToken();
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

  /* ========================================================================
   * Hibernate properties
   * ======================================================================== */

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

  /* ========================================================================
   * Dump/restore
   * ======================================================================== */

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

  /* ========================================================================
   * Mbean attributes
   * ======================================================================== */

  @Override
  public void setExport(final boolean val) {
    export = val;
  }

  @Override
  @MBeanInfo("Export (write) schema to database?")
  public boolean getExport() {
    return export;
  }

  /* ========================================================================
   * Operations
   * ======================================================================== */

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
      ((Processor)processor).addRequest(request);
    } catch (final Throwable t) {
      error(t);

      return false;
    }

    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public String schema() {
    try {
//      buildSchema = new SchemaThread();

      buildSchema.start();

      return "OK";
    } catch (Throwable t) {
      error(t);

      return "Exception: " + t.getLocalizedMessage();
    }
  }

  @Override
  public synchronized List<String> schemaStatus() {
    if (buildSchema == null) {
      InfoLines infoLines = new InfoLines();

      infoLines.addLn("Schema build has not been started");

      return infoLines;
    }

    return buildSchema.infoLines;
  }

  @Override
  public String listHibernateProperties() {
    StringBuilder res = new StringBuilder();

    List<String> ps = getConfig().getHibernateProperties();

    for (String p: ps) {
      res.append(p);
      res.append("\n");
    }

    return res.toString();
  }

  @Override
  public String displayHibernateProperty(final String name) {
    String val = getConfig().getHibernateProperty(name);

    if (val != null) {
      return val;
    }

    return "Not found";
  }

  @Override
  public synchronized List<String> restoreData() {
    List<String> infoLines = new ArrayList<String>();

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
    } catch (Throwable t) {
      error(t);
      infoLines.add("Exception - check logs: " + t.getMessage() + "\n");
    } finally {
      /*
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
    List<String> res = new ArrayList<>();

    res.add("************************Restore unimplemented *************************" + "\n");

    return res;
  }

  @Override
  public List<String> dumpData() {
    List<String> infoLines = new ArrayList<String>();

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
    } catch (Throwable t) {
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

  public String getStatus() {
    return status;
  }

  public void setStatus(final String val) {
    status = val;
  }

  @Override
  public boolean isRunning() {
    if (processor == null) {
      return false;
    }

    if (!processor.isAlive()) {
      processor = null;
      return false;
    }

    if (processor.getRunning()) {
      return true;
    }

    /* Kill it and return false */
    processor.interrupt();
    try {
      processor.join(5000);
    } catch (final Throwable ignored) {}

    if (!processor.isAlive()) {
      processor = null;
      return false;
    }

    warn("Processor was unstoppable. Acquiring new processor");
    processor = null;
    return false;
  }

  @Override
  public synchronized void start() {
    if (isRunning()) {
      error("Already started");
      return;
    }

    setStatus(statusStopped);

    try {
      processor = getProcessor();
    } catch (final Throwable t) {
      error("Error getting processor");
      error(t);
      return;
    }
    processor.setRunning(true);
    processor.start();
  }

  @Override
  public synchronized void stop() {
    if (processor == null) {
      error("Already stopped");
      return;
    }

    info("************************************************************");
    info(" * Stopping feeder");
    info("************************************************************");

    processor.setRunning(false);
    //?? ProcessorThread.stopProcess(processor);

    processor.interrupt();
    try {
      processor.join(20 * 1000);
    } catch (final InterruptedException ignored) {
    } catch (final Throwable t) {
      error("Error waiting for processor termination");
      error(t);
    }

    processor = null;

    info("************************************************************");
    info(" * Feeder terminated");
    info("************************************************************");
  }

  /* ====================================================================
   *                   Private methods
   * ==================================================================== */

  /**
   * @param val
   * @return 2 digit val
   */
  private static String twoDigits(final long val) {
    if (val < 10) {
      return "0" + val;
    }

    return String.valueOf(val);
  }

  private synchronized Configuration getHibConfiguration() {
    if (hibCfg == null) {
      try {
        hibCfg = new Configuration();

        StringBuilder sb = new StringBuilder();

        List<String> ps = getConfig().getHibernateProperties();

        for (String p: ps) {
          sb.append(p);
          sb.append("\n");
        }

        Properties hprops = new Properties();
        hprops.load(new StringReader(sb.toString()));

        hibCfg.addProperties(hprops).configure();
      } catch (Throwable t) {
        // Always bad.
        error(t);
      }
    }

    return hibCfg;
  }

  private EventregDb openDb() {
    try {
      EventregDb db = new EventregDb();

      db.open();

      return db;
    } catch (Throwable t) {
      error(t);
      return null;
    }
  }

  private void closeDb(final EventregDb db,
                       final boolean ignoreErrors) {
    db.close(ignoreErrors);
  }
}
