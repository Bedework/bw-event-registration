package org.bedework.eventreg.web;

import org.bedework.eventreg.db.EventregUserDb;
import org.bedework.eventreg.webcommon.EvregMethodHelper;

public abstract class EvregUserMethodHelper extends
        EvregMethodHelper {
  protected EvregUserMethod getUserMethodBase() {
    return (EvregUserMethod)getMethodBase();
  }

  protected EventregUserDb getEventregDb() {
    if (db != null) {
      return (EventregUserDb)db;
    }

    synchronized (this) {
      if (db != null) {
        return (EventregUserDb)db;
      }
      db = new EventregUserDb();
      db.setSysInfo(emb.getConfig());
    }

    return (EventregUserDb)db;
  }
}
