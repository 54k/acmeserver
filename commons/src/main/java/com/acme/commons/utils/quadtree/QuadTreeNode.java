package com.acme.commons.utils.quadtree;

import java.util.ArrayList;
import java.util.List;

public final class QuadTreeNode<T> {

	private final List<QuadTreeNode> surroundingNodes;
    private final T context;

    QuadTreeNode(T context) {
        this.context = context;
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
     * Returns context, associated with this node.
     *
     * @return associated context
     */
    public T getContext() {
        return context;
    }
}
