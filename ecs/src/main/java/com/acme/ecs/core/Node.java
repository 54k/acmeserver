package com.acme.ecs.core;

public interface Node {

    /**
     * Returns the owning entity of this node
     *
     * @return owning entity
     */
    Entity getEntity();
}
