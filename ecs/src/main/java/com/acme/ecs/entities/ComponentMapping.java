package com.acme.ecs.entities;

import com.acme.ecs.core.Component;

public interface ComponentMapping<T extends Component> {

    ComponentMapping<T> withInstance(T component);

    ComponentMapping<T> withType(Class<T> componentClass);

    ComponentMapping<T> withSingleton(Class<T> componentClass);

    ComponentMapping<T> withProvider(ComponentProvider<T> provider);
}
