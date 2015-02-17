package com.acme.server.brain;

import com.acme.engine.ashley.Wired;
import com.acme.engine.brain.BrainStateController;
import com.acme.server.combat.CombatController;
import com.acme.server.combat.HateListController;
import com.acme.server.combat.HateListListener;
import com.acme.server.combat.StatsController;
import com.acme.server.component.Spawn;
import com.acme.server.controller.PositionController;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatBrainState extends BrainStateController implements HateListListener {

    private ComponentMapper<Spawn> scm;

    private StatsController statsController;
    private PositionController positionController;
    private CombatController combatController;
    private HateListController hateListController;

    private PatrolBrainState patrolBrainState;

    private PacketSystem packetSystem;

    @Override
    public void update(Entity entity, float deltaTime) {
        if (statsController.isDead(entity)) {
            return;
        }

        Position position = positionController.getPosition(entity);
        // TODO this should go into spawn manager
        Position spawnPosition = scm.get(entity).getSpawnPosition();

        int distanceToFollow = 15;
        if (Math.abs(position.getX() - spawnPosition.getX()) >= distanceToFollow
                || Math.abs(position.getY() - spawnPosition.getY()) >= distanceToFollow) {
            startPatrol(entity);
            // TODO remove this hack later
            MovePacket movePacket = new MovePacket(entity);
            hateListController.getHaters(entity).getPlayers()
                    .forEach(p -> packetSystem.sendPacket(p, movePacket));
            return;
        }

        Entity target = combatController.getTarget(entity);
        Entity mostHated = hateListController.getMostHated(entity);

        if (target != mostHated) {
            combatController.setTarget(entity, mostHated);
            combatController.engage(entity, mostHated);
        } else {
            Position targetPosition = positionController.getPosition(target);
            positionController.updatePosition(entity, targetPosition);
        }
    }

    @Override
    public void exit(Entity entity) {
        if (!hateListController.getHaters(entity).isEmpty()) {
            hateListController.clearHaters(entity);
        }
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
        if (statsController.isDead(entity)) {
            changeState(entity, patrolBrainState);
        } else {
            startPatrol(entity);
        }
    }

    private void startPatrol(Entity entity) {
        Spawn spawnComponent = scm.get(entity);
        Position spawnPosition = spawnComponent.getSpawnPosition();
        positionController.moveEntity(entity, spawnPosition);
        changeState(entity, patrolBrainState);
    }
}
