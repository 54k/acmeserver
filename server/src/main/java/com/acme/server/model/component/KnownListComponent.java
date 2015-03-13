package com.acme.server.model.component;

import com.acme.commons.collections.NodeList;
import com.acme.ecs.core.Component;
import com.acme.server.model.node.PositionNode;

public class KnownListComponent extends Component {

	public final NodeList<PositionNode> knownNodes;
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
}
