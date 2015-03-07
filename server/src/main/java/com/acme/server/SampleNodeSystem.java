package com.acme.server;

import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Node;
import com.acme.engine.ecs.core.NodeListener;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.engine.ecs.utils.ImmutableList;
import com.acme.server.position.KnownListNode;

public class SampleNodeSystem extends PassiveSystem implements NodeListener {

    private ImmutableList<KnownListNode> nodes;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        Class<KnownListNode> nodeClass = KnownListNode.class;
        engine.addNodeListener(nodeClass, this);
        nodes = engine.getNodesFor(nodeClass);
    }

    @Override
    public void nodeAdded(Node node) {
        System.out.println(node + " has been added to world");
        System.out.println("Node count " + nodes.size());
    }

    @Override
    public void nodeRemoved(Node node) {
        System.out.println(node + " has been removed to world");
        System.out.println("Node count " + nodes.size());
    }
}
