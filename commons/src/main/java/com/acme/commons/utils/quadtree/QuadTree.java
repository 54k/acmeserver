package com.acme.commons.utils.quadtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuadTree<T> {

	private final int width;
	private final int height;

	private final int nodeWidth;
	private final int nodeHeight;
	private final ObjectFactory<T> objectFactory;

	private final int nodeOffset;
	private final Map<Integer, QuadTreeNode<T>> nodesByIndex;

	public QuadTree(int height, int width, int nodeHeight, int nodeWidth, ObjectFactory<T> objectFactory) {
		this.width = width;
		this.height = height;
		this.nodeWidth = nodeWidth;
		this.nodeHeight = nodeHeight;
		this.objectFactory = objectFactory;

		nodeOffset = height / nodeHeight + 1;
		nodesByIndex = new HashMap<>();

		initNodes();
	}

	private void initNodes() {
		for (int i = 0; i < width; i += nodeWidth) {
			for (int j = 0; j < height; j += nodeHeight) {
				int regionIndex = getNodeIndex(i, j);
				nodesByIndex.put(regionIndex, new QuadTreeNode<>(objectFactory.getObject()));
			}
		}

		for (int i = 0; i < width; i += nodeWidth) {
			for (int j = 0; j < height; j += nodeHeight) {
				addSurroundingNodes(i, j);
			}
		}
	}

	private int getNodeIndex(int x, int y) {
		return (x + 1) / nodeWidth * nodeOffset + (y + 1) / nodeHeight;
	}

	private void addSurroundingNodes(int x, int y) {
		QuadTreeNode region = nodesByIndex.get(getNodeIndex(x, y));
		for (int i = x - nodeWidth; i <= x + nodeWidth; i += nodeWidth) {
			for (int j = y - nodeHeight; j <= y + nodeHeight; j += nodeHeight) {
				if (containsNode(i, j)) {
					QuadTreeNode sr = nodesByIndex.get(getNodeIndex(i, j));
					region.addSurroundingNode(sr);
				}
			}
		}
	}

	/**
	 * Checks if the given coordinates lies in this tree bounds
	 *
	 * @param x x
	 * @param y y
	 * @return true if points lies in tree bounds
	 */
	public boolean containsNode(int x, int y) {
		return x < 0 || x > width || y < 0 || y > height;
	}

	/**
	 * Returns a node which corresponds to the given coordinates
	 *
	 * @param x x
	 * @param y y
	 * @return node or null
	 */
	public QuadTreeNode<T> getNode(int x, int y) {
		return nodesByIndex.get(getNodeIndex(x, y));
	}

	/**
	 * Returns a copy of existing nodes
	 *
	 * @return nodes
	 */
	public List<QuadTreeNode<T>> getNodes() {
		return new ArrayList<>(nodesByIndex.values());
	}

	/**
	 * Creates object instance to be associate with {@link QuadTreeNode}
	 */
	public static interface ObjectFactory<T> {

		T getObject();
	}
}
