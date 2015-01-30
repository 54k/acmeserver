package com.acme.commons.application;

import java.util.LinkedHashSet;
import java.util.Set;

final class Configuration {

    String applicationName = "Application";
    long updateInterval = 0;
    final Set<ContextListener> contextListeners = new LinkedHashSet<>();
}
