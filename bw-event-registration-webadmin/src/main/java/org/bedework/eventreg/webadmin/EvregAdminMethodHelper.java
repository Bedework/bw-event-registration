package org.bedework.eventreg.webadmin;

import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.webadmin.db.EventregAdminDb;
import org.bedework.eventreg.webcommon.EvregMethodHelper;

import java.util.List;

import static java.lang.String.format;

public abstract class EvregAdminMethodHelper extends
        EvregMethodHelper {
  protected EvregAdminMethod getAdminMethodBase() {
    return (EvregAdminMethod)getMethodBase();
  }

  protected EventregAdminDb getEventregDb() {
    if (db != null) {
      return (EventregAdminDb)db;
    }

    synchronized (this) {
      if (db != null) {
        return (EventregAdminDb)db;
      }
      db = new EventregAdminDb();
      db.setSysInfo(emb.getConfig());
    }

    return (EventregAdminDb)db;
  }

  public boolean ensureCalSuiteFormAbsent() {
    if (!requireCalsuite() || !requireFormName()) {
      return false; // for failure???
    }

    final var calsuite = globals.getCalsuite();
    final var formName = globals.getFormName();

    final var form =
            getEventregDb().getCalSuiteForm(formName,
                                            calsuite);

    if (form != null) {
      errorReturn(
              format("Form %s already exists for calsuite %s",
                     formName, calsuite));
      return false;
    }

    return true;
  }

  public List<FormDef> getCalSuiteForms() {
    if (!requireCalsuite()) {
      return null;
    }

    final var calsuite = globals.getCalsuite();

    return getEventregDb().getCalSuiteForms(calsuite);
  }
}
