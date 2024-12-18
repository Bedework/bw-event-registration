package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.webadmin.EvregAdminMethodBase;
import org.bedework.eventreg.webcommon.EvregMethodHelper;

public abstract class EvregAdminMethodHelper extends
        EvregMethodHelper {

  protected EvregAdminMethodBase getAdminMethodBase() {
    return (EvregAdminMethodBase)getMethodBase();
  }
}
