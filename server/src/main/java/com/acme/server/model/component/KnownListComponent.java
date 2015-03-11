package com.acme.server.model.component;

import com.acme.commons.utils.collections.NodeList;
import com.acme.ecs.core.Component;
import com.acme.server.model.node.TransformNode;

public class KnownListComponent extends Component {

    public final NodeList<TransformNode> knownNodes;
    public int distanceToFind;
    public int distanceToForget;

    public KnownListComponent() {
        this(-1, -1);
    }

    public KnownListComponent(int distanceToFind, int distanceToForget) {
        knownNodes = new NodeList<>();
        this.distanceToFind = distanceToFind;
        this.distanceToForget = distanceToForget;
    }

    public NodeList<TransformNode> getKnownNodes() {
        return knownNodes;
    }

    public void setDistanceToFind(int distanceToFind) {
        this.distanceToFind = distanceToFind;
    }

    public void setDistanceToForget(int distanceToForget) {
        this.distanceToForget = distanceToForget;
    }
}
