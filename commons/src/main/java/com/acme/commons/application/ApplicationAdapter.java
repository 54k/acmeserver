package com.acme.commons.application;

import com.acme.commons.ashley.WiringEngine;
import com.badlogic.ashley.core.Engine;

public abstract class ApplicationAdapter implements Application {

    private Context context;

    @Override
    public void create(Context context) {
        this.context = context;
        context.register(WiringEngine.class, new WiringEngine(context, new Engine()));
    }

    @Override
    public void update() {
        context.get(WiringEngine.class).update(context.getDelta());
    }

    @Override
    public void dispose() {
        context = null;
    }

    @Override
    public void handleError(Throwable t) {
    }

    public Context getContext() {
        return context;
    }
}
