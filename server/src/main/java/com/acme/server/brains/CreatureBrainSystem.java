package com.acme.server.brains;

import com.acme.ecs.core.*;
import com.acme.commons.brains.BrainSystem;
import com.acme.server.combat.HateListSystem;
import com.acme.server.entities.EntityBuilders;
import com.acme.server.position.Transform;

@Wire
public class CreatureBrainSystem extends BrainSystem implements EntityListener {

    private static final Family creaturesFamily = EntityBuilders.CREATURE_TYPE.getFamily();

    private ComponentMapper<Transform> positionCm;
    private Engine engine;
    private HateListSystem hateListSystem;

    public CreatureBrainSystem() {
        super(creaturesFamily);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(creaturesFamily, this);
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
        Transform transform = positionCm.get(entity);
        boolean isSpawned = transform.isSpawned();
        boolean isRegionActive = transform.getRegion().isActive();
        return isSpawned && (hateListSystem.hasHaters(entity) || isRegionActive);
    }
}
