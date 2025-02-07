package org.bedework.eventreg.service;

import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.EventregProperties;
import org.bedework.util.http.PooledHttpClient;
import org.bedework.util.http.RequestBuilder;

import java.net.URI;

import jakarta.servlet.http.HttpServletResponse;

public class EventregNotifier {
  // TODO - this needs to be somewhere it gets shut down properly
  private static PooledHttpClient http;

  public static boolean notify(final EventregProperties props,
                               final String href) {
    final var evregToken = props.getEventregAdminToken();
    final var evregUrl = props.getEventregWSUrl();

    if ((evregToken == null) || (evregUrl == null)) {
      // Cannot notify
      return false;
    }

    /* Send a notification to the event registration system that a
     * registerable event has changed. It's up to that system to
     * do something with it.
     */

    try {
      if (http == null) {
        http = new PooledHttpClient(new URI(evregUrl));
      }

      final RequestBuilder rb = new RequestBuilder(
              "eventChg");
      rb.par("atkn", evregToken);
      rb.par("href", href);

      final PooledHttpClient.ResponseHolder<?> resp =
              http.get(rb.toString(),
                       "application/xml",
                       null); // No content expected

      return resp.status == HttpServletResponse.SC_OK;
    } catch (final Throwable t) {
      throw new EventregException(t);
    }
  }
}
