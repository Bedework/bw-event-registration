package org.bedework.eventreg.service;

import org.bedework.util.jmx.ConfBase;

/** Class to obtain eventreg configuration.
 *
 */
public class Configuration extends ConfBase<EventregPropertiesImpl> {
  public Configuration(final String configDirectory,
                       final String configName) {
    super(null, "eventreg", null, "eventreg");
  }

  @Override
  public String loadConfig() {
    return loadConfig(EventregPropertiesImpl.class);
  }
}
