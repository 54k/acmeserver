package com.acme.commons.utils.quadtree;

import java.util.ArrayList;
import java.util.List;

public final class QuadTreeNode<T> {

	private final List<QuadTreeNode> surroundingNodes;
	private final T object;

	QuadTreeNode(T object) {
		this.object = object;
		surroundingNodes = new ArrayList<>(9);
	}

	void addSurroundingNode(QuadTreeNode node) {
		surroundingNodes.add(node);
	}

	/**
	 * Returns a copy of surrounding nodes with this node included
	 *
	 * @return nodes
	 */
	public List<QuadTreeNode> getSurroundingNodes() {
		return new ArrayList<>(surroundingNodes);
	}

	/**
	 * Returns an object, associated with this node
	 *
	 * @return associated object
	 */
	public T getObject() {
		return object;
	}
}
