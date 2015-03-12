package com.acme.server.model.component;

import com.acme.commons.timer.Timer;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

public class SpawnComponent extends Timer {

	public Position lastSpawnPosition;
	public Area area;

	public SpawnComponent() {
	}

	public SpawnComponent(Area area) {
		this.area = area;
	}

	public void refreshTimer() {
		setTime(Rnd.between(10000, 40000));
	}
}
