package com.acme.engine.aegis.core;

import com.acme.engine.aegis.events.EventListener;
import com.acme.engine.aegis.utils.ImmutableList;

class EventBinder implements Processor {

    @Override
    public void processSystems(ImmutableList<EntitySystem> systems, Engine engine) {
        for (EntitySystem system : systems) {
            processSystem(system, engine);
        }
    }

    @SuppressWarnings("unchecked")
    private void processSystem(EntitySystem system, Engine engine) {
        Class<?> systemClass = system.getClass();
        while (systemClass != null && EntitySystem.class.isAssignableFrom(systemClass)) {
            Class<?>[] interfaces = systemClass.getInterfaces();
            for (Class<?> i : interfaces) {
                if (EventListener.class.isAssignableFrom(i)) {
                    Class<EventListener> listenerClass = (Class<EventListener>) i;
                    engine.event(listenerClass).add((EventListener) system);
                }
            }
            systemClass = systemClass.getSuperclass();
        }
    }
}
