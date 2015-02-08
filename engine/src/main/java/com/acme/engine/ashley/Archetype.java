package com.acme.engine.ashley;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

public final class Archetype {

    private final Set<Class<? extends Component>> types = new HashSet<>();

    public Archetype() {
    }

    public Archetype(Archetype archetype) {
        types.addAll(archetype.types);
    }

    public Archetype add(Class<? extends Component> type) {
        types.add(type);
        return this;
    }

    public Entity build() {
        return addComponents(new Entity());
    }

    public Entity addComponents(Entity entity) {
        types.stream().map(Archetype::instantiate).forEach(entity::add);
        return entity;
    }

    public Family getFamily() {
        @SuppressWarnings("unchecked") Class<? extends Component>[] array = (Class<? extends Component>[]) Array.newInstance(Component.class.getClass(), types.size());
        return Family.all(types.toArray(array)).get();
    }

    private static Component instantiate(Class<? extends Component> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
