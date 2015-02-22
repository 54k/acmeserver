package com.acme.engine.application;

import com.acme.engine.aegis.core.Engine;

public abstract class ApplicationAdapter implements Application {

    private Context context;
    private Engine engine;

    @Override
    public void create(Context context) {
        this.context = context;
        engine = new Engine();
        context.register(Engine.class, engine);
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
}
