package com.acme.ecs.core;

public interface NodeListener {

    void nodeAdded(Node node);

    void nodeRemoved(Node node);
}
