package com.acme.engine.ecs.entities;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;

import java.util.HashMap;
import java.util.Map;

public class EntityBuilder {

    final Map<Class<? extends Component>, ComponentProvider<? extends Component>> providers = new HashMap<>();

    public EntityBuilder() {
    }

    public EntityBuilder(EntityBuilder parent) {
        providers.putAll(parent.providers);
    }

    public <T extends Component> ComponentMapper<T> add(Class<T> componentClass) {
        return new DefaultComponentMapper<>(providers, componentClass);
    }

    public <T extends Component> void remove(Class<T> componentClass) {
        providers.remove(componentClass);
    }

    public Family getFamily() {
        Family.Builder builder = new Family.Builder();
        for (Class<? extends Component> componentClass : providers.keySet()) {
            builder.all(componentClass);
        }
        return builder.get();
    }

    public Entity get() {
        Entity entity = new Entity();
        for (ComponentProvider<? extends Component> componentProvider : providers.values()) {
            Component component = componentProvider.getComponent();
            entity.add(component);
        }
        return entity;
    }
}
