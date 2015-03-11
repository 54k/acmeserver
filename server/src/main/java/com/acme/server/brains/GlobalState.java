package com.acme.server.brains;

import com.acme.commons.brains.Brain;
import com.acme.commons.brains.BrainState;
import com.acme.commons.brains.BrainStateMachine;
import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.server.combat.CombatSystem;
import com.acme.server.combat.HateListListener;
import com.acme.server.combat.HateListSystem;
import com.acme.server.combat.StatsSystem;
import com.acme.server.model.component.TransformComponent;
import com.acme.server.model.node.TransformNode;
import com.acme.server.model.system.PositionSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.MovePacket;
import com.acme.server.position.SpawnPoint;
import com.acme.server.world.Position;

@Wire
public class GlobalState implements BrainState<Entity>, HateListListener {

    private ComponentMapper<Brain> brainHolderCm;
    private ComponentMapper<SpawnPoint> spawnCm;

    private Engine engine;

    private StatsSystem statsSystem;
    private PositionSystem positionSystem;
    private CombatSystem combatSystem;
    private HateListSystem hateListSystem;
    private PacketSystem packetSystem;

    @Override
    public void enter(BrainStateMachine<Entity> brainStateMachine) {
        brainStateMachine.pushState(PatrolState.class);
    }

    @Override
    public void update(BrainStateMachine<Entity> brainStateMachine, float deltaTime) {
        Entity owner = brainStateMachine.getOwner();
        if (statsSystem.isDead(owner) && brainStateMachine.isInState(CombatState.class)) {
            brainStateMachine.popState();
        } else if (isToFarAwayFromSpawn(owner)) {
            getBrain(owner).popState();
            returnToSpawnPoint(owner);
            // TODO remove this hack later
            MovePacket movePacket = new MovePacket(owner);
            hateListSystem.getHaters(owner).getPlayers()
                    .forEach(p -> packetSystem.sendPacket(p, movePacket));
        }
    }

    @Override
    public void exit(BrainStateMachine<Entity> brainStateMachine) {
        engine.event(HateListListener.class).remove(this);
    }

    private boolean isToFarAwayFromSpawn(Entity entity) {
        Position position = entity.getComponent(TransformComponent.class).position;
        // TODO this should go into spawn manager
        Position spawnPosition = spawnCm.get(entity).getLastSpawnPosition();

        int distanceToFollow = 15;
        return Math.abs(position.getX() - spawnPosition.getX()) >= distanceToFollow
                || Math.abs(position.getY() - spawnPosition.getY()) >= distanceToFollow;
    }

    @Override
    public void onHaterAdded(Entity entity, Entity hater) {
        if (!isInState(entity, CombatState.class)) {
            getBrain(entity).pushState(CombatState.class);
        }
    }

    private boolean isInState(Entity entity, Class<? extends BrainState<Entity>> brainStateClass) {
        return getBrain(entity).isInState(brainStateClass);
    }

    @Override
    public void onHaterRemoved(Entity entity, Entity hater) {
    }

    @Override
    public void onHatersEmpty(Entity entity) {
        if (isInState(entity, CombatState.class)) {
            getBrain(entity).popState();
        }
        if (!statsSystem.isDead(entity)) {
            returnToSpawnPoint(entity);
        }
    }

    //    private void startPatrol(Entity entities) {
    //        returnToSpawnPoint(entities);
    //        getBrain(entities).changeState(PatrolState.class);
    //    }

    private void returnToSpawnPoint(Entity entity) {
        Position spawnPosition = spawnCm.get(entity).getLastSpawnPosition();
        positionSystem.moveTo(entity.getNode(TransformNode.class), spawnPosition);
    }

    private BrainStateMachine<Entity> getBrain(Entity entity) {
        return brainHolderCm.get(entity).getBrainStateMachine();
    }
}
