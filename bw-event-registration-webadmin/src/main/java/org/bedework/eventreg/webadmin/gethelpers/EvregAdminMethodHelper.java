package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.webadmin.EvregAdminMethod;
import org.bedework.eventreg.webcommon.EvregMethodHelper;

public abstract class EvregAdminMethodHelper extends
        EvregMethodHelper {

  protected EvregAdminMethod getAdminMethodBase() {
    return (EvregAdminMethod)getMethodBase();
  }
}
