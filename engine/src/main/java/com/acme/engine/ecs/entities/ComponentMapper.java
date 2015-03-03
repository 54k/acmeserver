package com.acme.engine.ecs.entities;

import com.acme.engine.ecs.core.Component;

public interface ComponentMapper<T extends Component> {

    ComponentMapper<T> withInstance(T component);

    ComponentMapper<T> withType(Class<T> componentClass);

    ComponentMapper<T> withSingleton(Class<T> componentClass);

    ComponentMapper<T> withProvider(ComponentProvider<T> provider);
}
