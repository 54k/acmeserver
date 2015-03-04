package com.acme.engine.ecs.core;

public interface NodeListener {

    void nodeAdded(Node node);

    void nodeRemoved(Node node);
}
