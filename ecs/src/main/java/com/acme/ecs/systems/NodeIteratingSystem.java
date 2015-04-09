package com.acme.ecs.systems;

import com.acme.ecs.core.Engine;
import com.acme.ecs.core.EntitySystem;
import com.acme.ecs.core.Node;
import com.acme.ecs.utils.ImmutableList;

public abstract class NodeIteratingSystem<T extends Node> extends EntitySystem {

    private Class<T> nodeClass;
    private ImmutableList<T> nodes;

    /**
     * Instantiates a system that will iterate over the entities described by the Family.
     *
     * @param nodeClass The node type of entities iterated over in this System
     */
    public NodeIteratingSystem(Class<T> nodeClass) {
        this(nodeClass, 0);
    }

    /**
     * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
     *
     * @param nodeClass The node type of entities iterated over in this System
     * @param priority  The priority to execute this system with (lower means higher priority)
     */
    public NodeIteratingSystem(Class<T> nodeClass, int priority) {
        super(priority);
        this.nodeClass = nodeClass;
    }

    @Override
    public void addedToEngine(Engine engine) {
        nodes = engine.getNodesFor(nodeClass);
    }

    /**
     * @return set of nodes processed by the system
     */
    public ImmutableList<T> getNodes() {
        return nodes;
    }

    @Override
    public void update(float deltaTime) {
        int size = nodes.size();
        for (int i = 0; i < size; ++i) {
            processNode(nodes.get(i), deltaTime);
        }
    }

    protected abstract void processNode(T node, float deltaTime);
}
