package com.acme.commons.application;

final class Configuration {

    private String applicationName = "Application";
    private long updateInterval = 0;

    public String getApplicationName() {
        return applicationName;
    }

    public Configuration setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public Configuration setUpdateInterval(long updateInterval) {
        if (updateInterval < 0) {
            throw new IllegalArgumentException("updateInterval must be >= 0");
        }
        this.updateInterval = updateInterval;
        return this;
    }
}
