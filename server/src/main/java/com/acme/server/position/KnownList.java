package com.acme.server.position;

import com.acme.commons.utils.collections.NodeList;
import com.acme.ecs.core.Component;

public class KnownList extends Component {

    public final NodeList<VisibleNode> visibleNodes;
    public int distanceToFind;
    public int distanceToForget;

    public KnownList() {
        this(-1, -1);
    }

    public KnownList(int distanceToFind, int distanceToForget) {
        visibleNodes = new NodeList<>();
        this.distanceToFind = distanceToFind;
        this.distanceToForget = distanceToForget;
    }

    public NodeList<VisibleNode> getKnownEntities() {
        return visibleNodes;
    }

    public int getDistanceToFindEntity() {
        return distanceToFind;
    }

    public void setDistanceToFind(int distanceToFind) {
        this.distanceToFind = distanceToFind;
    }

    public int getDistanceToForgetEntity() {
        return distanceToForget;
    }

    public void setDistanceToForget(int distanceToForget) {
        this.distanceToForget = distanceToForget;
    }
}
