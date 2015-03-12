package com.acme.server.brains;

import com.acme.commons.brains.BrainSystem;
import com.acme.ecs.core.*;
import com.acme.server.combat.HateListSystem;
import com.acme.server.entities.EntityBuilders;
import com.acme.server.model.component.PositionComponent;

@Wire
public class CreatureBrainSystem extends BrainSystem implements EntityListener {

	private static final Aspect CREATURES_ASPECT = EntityBuilders.CREATURE_TYPE.getAspect();

	private ComponentMapper<PositionComponent> positionCm;
	private Engine engine;
	private HateListSystem hateListSystem;

	public CreatureBrainSystem() {
		super(CREATURES_ASPECT);
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(CREATURES_ASPECT, this);
	}

	@Override
	public void entityAdded(Entity entity) {
	}

	@Override
	public void entityRemoved(Entity entity) {
		getBrainStateMachine(entity).clear();
	}

	@Override
	protected boolean shouldUpdateBrain(Entity entity, float deltaTime) {
		PositionComponent transform = positionCm.get(entity);
		return transform.spawned && (transform.region.isActive() || hateListSystem.hasHaters(entity));
	}
}
