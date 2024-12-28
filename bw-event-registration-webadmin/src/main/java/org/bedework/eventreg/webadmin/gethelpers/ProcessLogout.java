package org.bedework.eventreg.webadmin.gethelpers;

import org.bedework.eventreg.webadmin.EvregAdminMethodHelper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessLogout extends EvregAdminMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    globals.logout(getRequest());
    forward("success");
  }
}
