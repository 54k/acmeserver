package com.acme.commons.application;

import java.util.ArrayList;
import java.util.List;

final class Configuration {

    String applicationName = "Application";
    long updateInterval = 0;
    final List<ContextListener> contextListeners = new ArrayList<>();
}
