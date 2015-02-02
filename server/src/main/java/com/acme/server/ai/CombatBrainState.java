package com.acme.server.ai;

import com.acme.commons.ai.BrainState;
import com.acme.commons.ai.BrainStateController;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.HateComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.SpawnComponent;
import com.acme.server.controller.CombatController;
import com.acme.server.controller.HateController;
import com.acme.server.controller.PositionController;
import com.acme.server.event.HateEvents;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatBrainState extends BrainStateController implements BrainState, HateEvents {

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<SpawnComponent> scm;
    private ComponentMapper<HateComponent> hcm;

    private PositionController positionController;
    private CombatController combatController;
    private HateController hateController;

    private PatrolBrainState patrolBrainState;

    @Override
    public void update(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        SpawnComponent spawnComponent = scm.get(entity);

        Position position = positionComponent.getPosition();
        Position spawnPosition = spawnComponent.getSpawnPosition();

        int distanceToFollow = 20;
        if (Math.abs(position.getX() - spawnPosition.getX()) >= distanceToFollow
                || Math.abs(position.getY() - spawnPosition.getY()) >= distanceToFollow) {
            startPatrol(entity);
            return;
        }

        HateComponent hateComponent = hcm.get(entity);
        Entity target = hateComponent.getTarget();
        Entity mostHated = hateComponent.getMostHated();
        if (target != mostHated) {
            hateComponent.setTarget(mostHated);
            combatController.engage(entity, mostHated);
        } else {
            positionController.updatePosition(entity, pcm.get(target).getPosition());
        }
    }

    @Override
    public void exit(Entity entity) {
        hateController.clearHaters(entity);
    }

    @Override
    public void onHaterAdded(Entity entity, Entity hater) {
        changeState(entity, this);
    }

    @Override
    public void onHaterRemoved(Entity entity, Entity hater) {
    }

    @Override
    public void onHatersEmpty(Entity entity) {
        startPatrol(entity);
    }

    private void startPatrol(Entity entity) {
        SpawnComponent spawnComponent = scm.get(entity);
        Position spawnPosition = spawnComponent.getSpawnPosition();
        positionController.moveEntity(entity, spawnPosition);
        changeState(entity, patrolBrainState);
    }
}