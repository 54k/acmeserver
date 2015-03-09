package com.acme.ecs.entities;

import com.acme.ecs.core.Component;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Family;

import java.util.HashMap;
import java.util.Map;

public class EntityBuilder {

    final Map<Class<? extends Component>, ComponentProvider<? extends Component>> providers = new HashMap<>();

    public EntityBuilder() {
    }

    public EntityBuilder(EntityBuilder parent) {
        providers.putAll(parent.providers);
    }

    public <T extends Component> ComponentMapping<T> add(Class<T> componentClass) {
        return new DefaultComponentMapping<>(providers, componentClass);
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
            entity.addComponent(component);
        }
        return entity;
    }
}
