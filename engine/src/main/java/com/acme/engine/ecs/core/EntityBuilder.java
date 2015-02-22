package com.acme.engine.ecs.core;

import com.acme.engine.ecs.utils.reflection.ClassReflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityBuilder {

    private Set<Class<? extends Component>> componentTypes;

    public EntityBuilder() {
        componentTypes = new HashSet<>();
    }

    public EntityBuilder(EntityBuilder entityBuilder) {
        this();
        componentTypes.addAll(entityBuilder.componentTypes);
    }

    public EntityBuilder addComponentType(Class<? extends Component> componentType) {
        componentTypes.add(componentType);
        return this;
    }

    public Entity get() {
        return addComponentsTo(new Entity());
    }

    public Entity addComponentsTo(Entity entity) {
        for (Component component : createComponents()) {
            entity.add(component);
        }
        return entity;
    }

    private List<Component> createComponents() {
        List<Component> components = new ArrayList<>();
        for (Class<? extends Component> componentType : componentTypes) {
            Component component = ClassReflection.newInstance(componentType);
            components.add(component);
        }
        return components;
    }

    public Family getFamily() {
        Family.Builder builder = new Family.Builder();
        for (Class<? extends Component> componentType : componentTypes) {
            builder.all(componentType);
        }
        return builder.get();
    }
}
