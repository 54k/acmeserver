package com.acme.engine.ecs.core;

public interface NodeListener<T extends Node> {

    void nodeAdded(T node);

    void nodeRemoved(T node);
}
