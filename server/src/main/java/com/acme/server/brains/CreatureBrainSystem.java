package com.acme.server.brains;

import com.acme.engine.ecs.core.*;
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

    private CreatureGlobalState creatureGlobalState;

    public CreatureBrainSystem() {
        super(creaturesFamily);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(creaturesFamily, this);
    }

    @Override
    public void initialized() {
        creatureGlobalState = new CreatureGlobalState();
        engine.processObject(creatureGlobalState);
    }

    @Override
    public void entityAdded(Entity entity) {
        getBrain(entity).setGlobalState(creatureGlobalState);
    }

    @Override
    public void entityRemoved(Entity entity) {
        getBrain(entity).clear();
    }

    @Override
    protected boolean shouldUpdateBrain(Entity entity, float deltaTime) {
        PositionComponent positionComponent = positionCm.get(entity);
        boolean isSpawned = positionComponent.isSpawned();
        boolean isRegionActive = positionComponent.getRegion().isActive();
        return isSpawned && (hateListController.hasHaters(entity) || isRegionActive);
    }
}
