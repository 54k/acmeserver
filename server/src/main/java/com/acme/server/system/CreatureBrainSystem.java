package com.acme.server.system;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brain.BrainSystem;
import com.acme.server.brain.CombatBrainState;
import com.acme.server.brain.PatrolBrainState;
import com.acme.server.combat.HateListController;
import com.acme.server.component.PositionComponent;
import com.acme.server.entity.Archetypes;

@Wire
public class CreatureBrainSystem extends BrainSystem {

    private ComponentMapper<PositionComponent> pcm;

    private HateListController hateListController;

    public CreatureBrainSystem() {
        super(Archetypes.CREATURE_TYPE.getFamily(), new PatrolBrainState(), new CombatBrainState());
    }

    @Override
    protected boolean shouldUpdateBrain(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        boolean isSpawned = positionComponent.isSpawned();
        boolean isRegionActive = positionComponent.getRegion().isActive();
        return isSpawned && (hateListController.hasHaters(entity) || isRegionActive);
    }
}
