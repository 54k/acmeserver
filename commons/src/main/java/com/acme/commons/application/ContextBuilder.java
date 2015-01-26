package com.acme.commons.application;

public final class ContextBuilder {

    private final Application application;
    private final Configuration configuration = new Configuration();

    public ContextBuilder(Application application) {
        this.application = application;
    }

    public ContextBuilder setApplicationName(String applicationName) {
        configuration.setApplicationName(applicationName);
        return this;
    }

    public ContextBuilder setUpdateInterval(long updateInterval) {
        configuration.setUpdateInterval(updateInterval);
        return this;
    }

    public Context build() {
        return new UpdateLoop(application, configuration);
    }
}
