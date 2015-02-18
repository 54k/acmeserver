package com.acme.engine.aegis;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

public class Archetype {

    private Set<Class<? extends Component>> components;

    public Archetype() {
        components = new HashSet<>();
    }

    public Archetype(Archetype archetype) {
        this();
        components.addAll(archetype.components);
    }

    public Archetype add(Class<? extends Component> type) {
        components.add(type);
        return this;
    }

    public Entity buildEntity() {
        return addComponents(new Entity());
    }

    public Entity addComponents(Entity entity) {
        components.stream().map(Archetype::instantiate).forEach(entity::add);
        return entity;
    }

    @SuppressWarnings("unchecked")
    public Family getFamily() {
        Class<? extends Component>[] array = (Class<? extends Component>[]) Array.newInstance(Component.class, components.size());
        return Family.all(components.toArray(array)).get();
    }

    private static Component instantiate(Class<? extends Component> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
