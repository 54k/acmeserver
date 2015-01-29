package com.acme.commons.event;

import com.acme.commons.ashley.ManagerSystem;

public class EventManager extends ManagerSystem {

    private final EventBus eventBus = new EventBus();

    public <T> T post(Class<T> type) {
        return eventBus.post(type);
    }

    public void register(Object listener) {
        eventBus.register(listener);
    }

    public void unregister(Object listener) {
        eventBus.unregister(listener);
    }
}
