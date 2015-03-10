package com.acme.commons.utils.collections;

import com.acme.ecs.core.Node;

import java.util.ArrayList;
import java.util.Collection;

public class NodeList<T extends Node> extends ArrayList<T> implements Queried<T> {

    public NodeList(int initialCapacity) {
        super(initialCapacity);
    }

    public NodeList() {
    }

    public NodeList(Collection<? extends T> c) {
        super(c);
    }

    @Override
    public NodeList<T> query(Predicate predicate) {
        NodeList<T> nodes = new NodeList<>();
        for (T node : this) {
            if (predicate.matches(node.getEntity())) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    @Override
    public T querySingle(Predicate predicate) {
        for (T node : this) {
            if (predicate.matches(node.getEntity())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Transforms entities to the {@link EntityList}
     */
    public EntityList transform() {
        EntityList entities = new EntityList();
        for (T node : this) {
            entities.add(node.getEntity());
        }
        return entities;
    }
}
