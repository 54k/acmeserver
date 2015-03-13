package com.acme.commons.utils.quadtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class QuadTreeRegion<T extends Collection> {

	private final List<QuadTreeRegion> surroundingRegions;
	private final T storage;

	QuadTreeRegion(T storage) {
		this.storage = storage;
		surroundingRegions = new ArrayList<>(9);
	}

	void addSurroundingRegion(QuadTreeRegion region) {
		surroundingRegions.add(region);
	}

	/**
	 * Returns a copy of surrounding regions with this region included
	 *
	 * @return regions
	 */
	public List<QuadTreeRegion> getSurroundingRegions() {
		return new ArrayList<>(surroundingRegions);
	}

	/**
	 * Returns items storage, associated with this region
	 *
	 * @return storage
	 */
	public T getStorage() {
		return storage;
	}
}
