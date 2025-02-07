package org.bedework.eventreg.ws.gethelpers;

import org.bedework.eventreg.webcommon.EvregMethodHelper;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProcessIsAdmin extends EvregMethodHelper {
  @Override
  public void evProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    final String[] adminUsers =
            getMethodBase().getConfig()
                           .getAdminUsers()
                           .split(",");
    if (adminUsers.length == 0) {
      warn("No admin users defined");
      sendJsonError(resp, "No admin users defined");
      return;
    }

    if (!Arrays.asList(adminUsers).contains(globals.getCurrentUser())) {
      sendJsonError(resp, "Not an admin user");
    } else {
      getMethodBase().sendOkJsonData(getRequest().getResponse());
    }
  }
}
