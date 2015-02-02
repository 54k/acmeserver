package com.acme.server.system;

import com.acme.commons.ai.BrainSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.ai.CombatState;
import com.acme.server.ai.PatrolState;
import com.acme.server.component.PositionComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

@Wired
public class CreatureBrainSystem extends BrainSystem {

    private ComponentMapper<PositionComponent> pcm;

    public CreatureBrainSystem() {
        super(Family.all(PositionComponent.class).get(), new PatrolState(), new CombatState());
    }

    @Override
    protected boolean shouldUpdateBrain(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        boolean isSpawned = positionComponent.isSpawned();
        boolean isRegionActive = positionComponent.getRegion().isActive();
        return isSpawned && isRegionActive;
    }
}
