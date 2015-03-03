package com.acme.engine.ecs.entities;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.ComponentType;
import com.acme.engine.ecs.utils.reflection.ClassReflection;

public class ComponentSingletonProvider<T extends Component> implements ComponentProvider<T> {

    private final Class<T> componentClass;
    private T instance;

    public ComponentSingletonProvider(Class<T> componentClass) {
        this.componentClass = componentClass;
    }

    @Override
    public T getComponent() {
        if (instance == null) {
            instance = ClassReflection.newInstance(componentClass);
        }
        return instance;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.getFor(componentClass);
    }
}
