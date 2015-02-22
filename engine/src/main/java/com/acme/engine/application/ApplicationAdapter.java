package com.acme.engine.application;

import com.acme.engine.ecs.core.Engine;

public abstract class ApplicationAdapter implements Application {

    private Context context;
    private Engine engine;

    @Override
    public void create(Context context) {
        this.context = context;
        engine = new Engine();
    }

    @Override
    public void update() {
        engine.update(context.getDelta());
    }

    @Override
    public void dispose() {
        context = null;
        engine = null;
    }

    @Override
    public void handleError(Throwable t) {
    }

    public Context getContext() {
        return context;
    }

    public Engine getEngine() {
        return engine;
    }
}
