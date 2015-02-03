package com.acme.core.application;

import com.acme.core.ashley.EntityEngine;
import com.badlogic.ashley.core.Engine;

public abstract class ApplicationAdapter implements Application {

    private Context context;

    @Override
    public void create(Context context) {
        this.context = context;
        context.register(EntityEngine.class, new EntityEngine(context, new Engine()));
    }

    @Override
    public void update() {
        context.get(EntityEngine.class).update(context.getDelta());
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
