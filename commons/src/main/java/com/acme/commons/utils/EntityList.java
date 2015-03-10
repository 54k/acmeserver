package com.acme.commons.utils;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Family;
import com.acme.ecs.core.Node;
import com.acme.ecs.core.NodeFamily;

import java.util.ArrayList;
import java.util.List;

public class EntityList extends ArrayList<Entity> {

    /**
     * Retrieves entity by id
     *
     * @param id id
     * @return entity with the given id or null
     */
    public Entity getBy(long id) {
        for (Entity entity : this) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Queries and retrieves an entities, matched by the given node class
     *
     * @param nodeClass node class
     * @return list with retrieved entities
     */
    public <T extends Node> List<T> queryBy(Class<T> nodeClass) {
        List<T> nodes = new ArrayList<>();
        NodeFamily<T> family = NodeFamily.getFor(nodeClass);
        for (Entity entity : this) {
            if (family.matches(entity)) {
                nodes.add(family.get(entity));
            }
        }
        return nodes;
    }

    /**
     * Queries and retrieves an entities, matched by the given family
     *
     * @param family family
     * @return list with retrieved entities
     */
    public List<Entity> queryBy(Family family) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : this) {
            if (family.matches(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
