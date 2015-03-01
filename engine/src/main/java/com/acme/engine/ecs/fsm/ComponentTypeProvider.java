package com.acme.engine.ecs.fsm;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.ComponentType;
import com.acme.engine.ecs.utils.reflection.ClassReflection;

public class ComponentTypeProvider<T extends Component> implements ComponentProvider<T> {

    private final Class<T> componentClass;

    public ComponentTypeProvider(Class<T> componentClass) {
        this.componentClass = componentClass;
    }

    @Override
    public T getComponent() {
        return ClassReflection.newInstance(componentClass);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.getFor(componentClass);
    }
}
