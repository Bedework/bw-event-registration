package org.bedework.eventreg.webcommon;

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.util.servlet.MethodHelper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class EvregMethodHelper extends MethodHelper {
  private EvregMethodBase emb;
  protected WebGlobals globals;

  public abstract void evProcess(List<String> resourceUri,
                                 HttpServletRequest req,
                                 HttpServletResponse resp);
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    emb = (EvregMethodBase)super.getMethodBase();
    globals = emb.getWebGlobals();
    evProcess(resourceUri, req, resp);
  }

  protected EvregMethodBase getMethodBase() {
    return emb;
  }

  protected Request getRequest() {
    return emb.getRequest();
  }

  protected EventregDb getEventregDb() {
    return emb.getEventregDb();
  }

  protected BwConnector getConnector() {
    return emb.getConnector();
  }

  protected void errorReturn(final Throwable t) {
    errorReturn(t.getLocalizedMessage());
  }

  protected void errorReturn(final String msg) {
    errorReturn("/docs/error.jsp", msg);
  }

  protected void errorReturn(final String forward,
                                     final String msg) {
    globals.setMessage(msg);
    getMethodBase().forward(forward);
  }
}
