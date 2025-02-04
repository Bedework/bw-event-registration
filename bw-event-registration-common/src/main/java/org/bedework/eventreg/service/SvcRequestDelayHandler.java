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

import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.eventreg.requests.EventregRequest;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.jms.events.SysEvent;
import org.bedework.util.jms.listeners.JmsSysEventListener;
import org.bedework.util.misc.AbstractProcessorThread;
import org.bedework.util.misc.Util;

import static org.bedework.util.misc.AbstractProcessorThread.stopProcess;

/** This just delays requests that appear to be having problems. They
 * get requeued after a period of time.
 *
 * <p>This is a simple queue in which each event is delayed for the same
 * amount of time - so events can just be queued and the listener can
 * fetch an event from the front and wait till it's expiry time.
 *
 * <p>At that point it is requeued on the main handler</p>
 *
 * @author douglm
 *
 */
public class SvcRequestDelayHandler extends JmsSysEventListener {
  private final EventregProperties props;

  private final SvcRequestHandler handler;

  private final EventregSenderHandler sender;

  private static class Processor extends AbstractProcessorThread {
    private final SvcRequestDelayHandler handler;

    /**
     * @param name for the thread
     */
    public Processor(final String name,
                     final SvcRequestDelayHandler handler) {
      super(name);
      this.handler = handler;
    }

    @Override
    public void runInit() {
    }

    @Override
    public void runProcess() {
      handler.listen();
      running = false;
    }

    @Override
    public void end(final String msg) {
    }

    @Override
    public void close() {
      handler.close();
    }
  }

  private AbstractProcessorThread processor;

  public SvcRequestDelayHandler(final SvcRequestHandler handler,
                                final EventregProperties props) {
    this.props = props;
    this.handler = handler;

    if (props.getDelayMillis() <= 0) {
      warn("Bad or unset value for delayMillis: set to 30 seconds");
      props.setDelayMillis(30 * 1000);
    }

    sender = new SvcRequestSender(props);
  }

  @Override
  public void action(final SysEvent ev) {
    if (ev == null) {
      return;
    }

    try {
      if (debug()) {
        debug("handling delayed request: " + ev);
      }

      if (!(ev instanceof final EventregRequest req)) {
        return;
      }

      try {
        final long waitTime = req.getWaitUntil() -
                System.currentTimeMillis();

        if (waitTime > 0) {
          if (debug()) {
            debug("Waiting " + waitTime + " millis");
          }

          synchronized (this) {
            this.wait(waitTime);
          }
        }

        // Requeue
        handler.addRequest(req);
      } catch (final Throwable t) {
        error("Error handling delayed request: " + req);
        error(t);
      }
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }

  public void close() {
//    stop();
    super.close();
  }

  @SuppressWarnings("UnusedReturnValue")
  public boolean delay(final EventregRequest req) {
    if (req.getDiscard()) {
      warn("Discarding: " + req);
      return false;
    }

    req.incRetries();

    final int maxRetries = props.getRetries();

    if ((maxRetries > 0) && (req.getRetries() > maxRetries)) {
      warn("Discarding - too many retries: " + req);
      return false;
    }

    req.setWaitUntil(System.currentTimeMillis() + props.getDelayMillis());

    sender.addRequest(req);
    return true;
  }

  public void listen() {
    try {
      open(props.getActionDelayQueueName(),
           ConfigBase.toProperties(props.getSyseventsProperties()));

      process(false);

      if (debug()) {
        debug("Eventregdelay returned from process");
      }

    } catch (final Throwable t) {
      if (Util.causeIs(t, InterruptedException.class)) {
        warn("Received interrupted exception");
      } else {
        error(t);
      }
      throw new EventregException(t);
    }
  }

  AbstractProcessorThread getProcessor() {
    return new Processor("EventregActionDelay", this);
  }

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
    stopProcess(processor);
    if (!processor.isAlive()) {
      processor = null;
      return false;
    }

    warn("Processor was unstoppable. Acquiring new processor");
    processor = null;
    return false;
  }

  public synchronized void start() {
    if (isRunning()) {
      error("Already started");
      return;
    }

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

  public synchronized void stop() {
    info("Stop called for " + getClass().getSimpleName());
    if (processor == null) {
      error("Already stopped");
      return;
    }

    stopProcess(processor);
    processor = null;
  }
}
