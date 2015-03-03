package com.acme.engine.ecs.entities;

import com.acme.engine.ecs.core.Component;

public interface ComponentMapping<T extends Component> {

    ComponentMapping<T> withInstance(T component);

    ComponentMapping<T> withType(Class<T> componentClass);

    ComponentMapping<T> withSingleton(Class<T> componentClass);

    ComponentMapping<T> withProvider(ComponentProvider<T> provider);
}
