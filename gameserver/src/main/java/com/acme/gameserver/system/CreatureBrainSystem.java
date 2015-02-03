package com.acme.gameserver.system;

import com.acme.core.ai.BrainSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.ai.CombatBrainState;
import com.acme.gameserver.ai.PatrolBrainState;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.controller.HateController;
import com.acme.gameserver.entity.Archetypes;
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
