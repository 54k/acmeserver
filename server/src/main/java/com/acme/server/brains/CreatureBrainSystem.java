package com.acme.server.brains;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.EntityListener;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainSystem;
import com.acme.server.combat.HateListController;
import com.acme.server.component.PositionComponent;
import com.acme.server.entity.Archetypes;

@Wire
public class CreatureBrainSystem extends BrainSystem implements EntityListener {

    private static final Family creaturesFamily = Archetypes.CREATURE_TYPE.getFamily();

    private ComponentMapper<PositionComponent> positionCm;
    private Engine engine;
    private HateListController hateListController;

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
        PositionComponent positionComponent = positionCm.get(entity);
        boolean isSpawned = positionComponent.isSpawned();
        boolean isRegionActive = positionComponent.getRegion().isActive();
        return isSpawned && (hateListController.hasHaters(entity) || isRegionActive);
    }
}
