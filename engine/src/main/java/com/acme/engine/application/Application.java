package com.acme.engine.application;

public interface Application {

    void create(Context context);

    void update();

    void dispose();

    void handleError(Throwable t);
}
