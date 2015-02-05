package com.acme.server.ai;

import com.acme.commons.ai.BrainState;
import com.acme.commons.ai.BrainStateController;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.SpawnComponent;
import com.acme.server.controller.CombatController;
import com.acme.server.controller.HateController;
import com.acme.server.controller.PositionController;
import com.acme.server.event.HateControllerEvent;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatBrainState extends BrainStateController implements BrainState, HateControllerEvent {

    private ComponentMapper<SpawnComponent> scm;

    private PositionController positionController;
    private CombatController combatController;
    private HateController hateController;

    private PatrolBrainState patrolBrainState;

    private PacketSystem packetSystem;

    @Override
    public void update(Entity entity, float deltaTime) {
        Position position = positionController.getPosition(entity);
        // TODO this should go into spawn manager
        Position spawnPosition = scm.get(entity).getSpawnPosition();

        int distanceToFollow = 15;
        if (Math.abs(position.getX() - spawnPosition.getX()) >= distanceToFollow
                || Math.abs(position.getY() - spawnPosition.getY()) >= distanceToFollow) {
            startPatrol(entity);
            // TODO remove this hack later
            MovePacket movePacket = new MovePacket(entity);
            hateController.getHaters(entity).getPlayers().values()
                    .forEach(p -> packetSystem.sendPacket(p, movePacket));
            return;
        }

        Entity target = hateController.getTarget(entity);
        Entity mostHated = hateController.getMostHated(entity);

        if (target != mostHated) {
            hateController.setTarget(entity, mostHated);
            combatController.engage(entity, mostHated);
        } else {
            Position targetPosition = positionController.getPosition(target);
            positionController.updatePosition(entity, targetPosition);
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
