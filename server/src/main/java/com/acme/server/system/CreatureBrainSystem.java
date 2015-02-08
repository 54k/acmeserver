package com.acme.server.system;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.BrainSystem;
import com.acme.server.ai.CombatBrainState;
import com.acme.server.ai.PatrolBrainState;
import com.acme.server.component.PositionComponent;
import com.acme.server.controller.HateController;
import com.acme.server.entity.Archetypes;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CreatureBrainSystem extends BrainSystem {

    private ComponentMapper<PositionComponent> pcm;

    private HateController hateController;

    public CreatureBrainSystem() {
        super(Archetypes.CREATURE_TYPE.getFamily(), new PatrolBrainState(), new CombatBrainState());
    }

    @Override
    protected boolean shouldUpdateBrain(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        boolean isSpawned = positionComponent.isSpawned();
        boolean isRegionActive = positionComponent.getRegion().isActive();
        return isSpawned && (hateController.hasHaters(entity) || isRegionActive);
    }
}
