package com.acme.commons.utils.quadtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuadTree<T extends Collection> {

	private final int width;
	private final int height;

	private final int regionWidth;
	private final int regionHeight;
	private final StorageFactory<T> storageFactory;

	private final int regionOffset;
	private final Map<Integer, QuadTreeRegion<T>> regionsByIndex;

	public QuadTree(int height, int width, int regionHeight, int regionWidth, StorageFactory<T> storageFactory) {
		this.width = width;
		this.height = height;
		this.regionWidth = regionWidth;
		this.regionHeight = regionHeight;
		this.storageFactory = storageFactory;

		regionOffset = height / regionHeight + 1;
		regionsByIndex = new HashMap<>();

		initRegions();
	}

	private void initRegions() {
		for (int i = 0; i < width; i += regionWidth) {
			for (int j = 0; j < height; j += regionHeight) {
				int regionIndex = getRegionIndex(i, j);
				regionsByIndex.put(regionIndex, new QuadTreeRegion<>(storageFactory.newStorage()));
			}
		}

		for (int i = 0; i < width; i += regionWidth) {
			for (int j = 0; j < height; j += regionHeight) {
				addSurroundingRegions(i, j);
			}
		}
	}

	private int getRegionIndex(int x, int y) {
		return (x + 1) / regionWidth * regionOffset + (y + 1) / regionHeight;
	}

	private void addSurroundingRegions(int x, int y) {
		QuadTreeRegion region = regionsByIndex.get(getRegionIndex(x, y));
		for (int i = x - regionWidth; i <= x + regionWidth; i += regionWidth) {
			for (int j = y - regionHeight; j <= y + regionHeight; j += regionHeight) {
				if (isValidRegionPosition(i, j)) {
					QuadTreeRegion sr = regionsByIndex.get(getRegionIndex(i, j));
					region.addSurroundingRegion(sr);
				}
			}
		}
	}

	private boolean isValidRegionPosition(int x, int y) {
		return x < 0 || x > width || y < 0 || y > height;
	}

	/**
	 * Returns a region which corresponds to the given position
	 *
	 * @param x x position
	 * @param y y position
	 * @return region or null
	 */
	public QuadTreeRegion<T> getRegion(int x, int y) {
		return regionsByIndex.get(getRegionIndex(x, y));
	}

	/**
	 * Returns a copy of existing regions
	 *
	 * @return regions
	 */
	public List<QuadTreeRegion<T>> getRegions() {
		return new ArrayList<>(regionsByIndex.values());
	}

	public static interface StorageFactory<T extends Collection> {
		T newStorage();
	}
}
