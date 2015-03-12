package com.acme.server.model.system.active;

import com.acme.commons.timer.TimerSystem;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.server.combat.StatsSystem;
import com.acme.server.model.component.SpawnComponent;
import com.acme.server.model.component.PositionComponent;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.node.PositionNode;
import com.acme.server.model.node.SpawnNode;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.system.passive.PositionSystem;
import com.acme.server.model.system.passive.WorldSystem;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;

public class SpawnSystem extends TimerSystem<SpawnComponent> {

	@Wire
	private StatsSystem statsSystem;
	@Wire
	private WorldSystem worldSystem;

	@Wire
	private PositionSystem positionSystem;

	public SpawnSystem() {
		super(SpawnComponent.class);
	}

	@Override
	protected boolean shouldTickTimer(Entity entity, float deltaTime) {
		return !entity.getNode(PositionNode.class).getPosition().spawned;
	}

	@Override
	protected void timerReady(Entity entity, float deltaTime) {
		if (StatsSystem.STATS_ASPECT.matches(entity)) {
			statsSystem.resetHitPoints(entity);
		}
		spawn(entity.getNode(SpawnNode.class));
	}

	public void spawn(SpawnNode spawnNode) {
		PositionComponent transform = spawnNode.getPosition();
		transform.orientation = randomOrientation();

		SpawnComponent spawn = spawnNode.getSpawn();
		spawn.refreshTimer();

		WorldComponent world = spawnNode.getWorld();
		Position randomSpawnPosition = getRandomSpawnPosition(spawn.area, world.instance);
		spawn.lastSpawnPosition = randomSpawnPosition;
		transform.position.setPosition(randomSpawnPosition);
		positionSystem.spawn(spawnNode);
	}

	private static Orientation randomOrientation() {
		int randomOrientation = Rnd.between(0, Orientation.values().length - 1);
		return Orientation.values()[randomOrientation];
	}

	private static Position getRandomSpawnPosition(Area area, Instance instance) {
		Position spawnPosition;
		do {
			spawnPosition = PositionUtils.getRandomPositionInside(area);
		} while (instance.getWorld().isColliding(spawnPosition.getX(), spawnPosition.getY()));
		return spawnPosition;
	}
}
