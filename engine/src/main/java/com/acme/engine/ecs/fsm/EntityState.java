package com.acme.engine.ecs.fsm;

import com.acme.engine.ecs.core.Component;

import java.util.HashMap;
import java.util.Map;

public class EntityState {

    final Map<Class<? extends Component>, ComponentProvider<? extends Component>> providers;

    public EntityState() {
        providers = new HashMap<>();
    }

    /**
     * Add a new ComponentMapping to this state. The mapping is a utility class that is used to
     * map a component type to the provider that provides the component.
     *
     * @param componentClass The type of component to be mapped
     * @return The component mapping to use when setting the provider for the component
     */
    public <T extends Component> StateComponentMapping<T> add(Class<T> componentClass) {
        return new StateComponentMapping<>(this, componentClass);
    }
}
