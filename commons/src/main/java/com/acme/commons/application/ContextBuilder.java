package com.acme.commons.application;

public final class ContextBuilder {

    private final Application application;
    private final Configuration configuration = new Configuration();

    public ContextBuilder(Application application) {
        this.application = application;
    }

    public ContextBuilder setApplicationName(String applicationName) {
        configuration.applicationName = applicationName;
        return this;
    }

    public ContextBuilder setUpdateInterval(long updateInterval) {
        if (updateInterval < 0) {
            throw new IllegalArgumentException("updateInterval must be >= 0");
        }
        configuration.updateInterval = updateInterval;
        return this;
    }

    public ContextBuilder addContextListener(ContextListener contextListener) {
        configuration.contextListeners.add(contextListener);
        return this;
    }

    public Context build() {
        return new UpdateLoop(application, configuration);
    }
}
