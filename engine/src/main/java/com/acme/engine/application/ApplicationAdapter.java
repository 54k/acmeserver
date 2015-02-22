package com.acme.engine.application;

import com.acme.engine.ecs.core.Engine;

public abstract class ApplicationAdapter implements Application {

    private volatile Context context;
    private volatile Engine engine;

    public ApplicationAdapter() {
        context = new UpdateLoop(this, 60);
        engine = new Engine();
    }

    public Context getContext() {
        return context;
    }

    public Engine getEngine() {
        return engine;
    }

    public final void start() {
        context.start();
        context.waitForStart(0);
    }

    public final void stop() {
        context.dispose();
        context.waitForDispose(0);
    }

    @Override
    public void create(Context context) {
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
}
