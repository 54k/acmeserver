package com.acme.commons.collections;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Node;
import com.acme.ecs.core.NodeFamily;

import java.util.ArrayList;
import java.util.Collection;

public class EntityList extends ArrayList<Entity> implements Queried<Entity> {

    public EntityList() {
    }

    public EntityList(Collection<? extends Entity> c) {
        super(c);
    }

    public EntityList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public EntityList query(Predicate predicate) {
        EntityList entities = new EntityList();
        for (Entity entity : this) {
            if (predicate.matches(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public Entity querySingle(Predicate predicate) {
        for (Entity entity : this) {
            if (predicate.matches(entity)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Transforms entities to the {@link NodeList} with the given node class
     *
     * @param nodeClass node class
     */
    public <T extends Node> NodeList<T> transform(Class<T> nodeClass) {
        NodeFamily<T> family = NodeFamily.getFor(nodeClass);
        NodeList<T> entities = new NodeList<>();
        for (Entity entity : this) {
            entities.add(family.get(entity));
        }
        return entities;
    }
}
