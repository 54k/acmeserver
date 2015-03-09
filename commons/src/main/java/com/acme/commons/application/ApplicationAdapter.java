package com.acme.commons.application;

import com.acme.ecs.core.Engine;

public abstract class ApplicationAdapter implements Application {

    private final Context context;
    private final Engine engine;

    protected ApplicationAdapter() {
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
    }

    @Override
    public void handleError(Throwable t) {
    }
}
