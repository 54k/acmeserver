package com.acme.core.application;

public interface Application {

    void create(Context context);

    void update();

    void dispose();

    void handleError(Throwable t);
}
