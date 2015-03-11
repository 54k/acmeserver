package com.acme.server.brains;

import com.acme.commons.brains.BrainSystem;
import com.acme.ecs.core.*;
import com.acme.server.combat.HateListSystem;
import com.acme.server.entities.EntityBuilders;
import com.acme.server.model.component.TransformComponent;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.node.WorldNode;

@Wire
public class CreatureBrainSystem extends BrainSystem implements EntityListener {

    private static final Aspect CREATURES_ASPECT = EntityBuilders.CREATURE_TYPE.getAspect();

    private ComponentMapper<TransformComponent> positionCm;
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
        TransformComponent transform = positionCm.get(entity);
        WorldComponent world = entity.getNode(WorldNode.class).getWorld();
        boolean isSpawned = world.spawned;
        boolean isRegionActive = world.region.isActive();
        return isSpawned && (hateListSystem.hasHaters(entity) || isRegionActive);
    }
}
