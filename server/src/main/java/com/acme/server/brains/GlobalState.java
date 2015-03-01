package com.acme.server.brains;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainHolder;
import com.acme.engine.mechanics.brains.BrainState;
import com.acme.engine.mechanics.brains.BrainStateMachine;
import com.acme.server.combat.CombatController;
import com.acme.server.combat.HateListController;
import com.acme.server.combat.HateListListener;
import com.acme.server.combat.StatsController;
import com.acme.server.component.Spawn;
import com.acme.server.controller.PositionController;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;

@Wire
public class GlobalState implements BrainState<Entity>, HateListListener {

    private ComponentMapper<BrainHolder> brainHolderCm;
    private ComponentMapper<Spawn> spawnCm;

    private Engine engine;

    private StatsController statsController;
    private PositionController positionController;
    private CombatController combatController;
    private HateListController hateListController;
    private PacketSystem packetSystem;

    @Override
    public void enter(BrainStateMachine<Entity> brainStateMachine) {
        brainStateMachine.pushState(PatrolState.class);
    }

    @Override
    public void update(BrainStateMachine<Entity> brainStateMachine, float deltaTime) {
        Entity owner = brainStateMachine.getOwner();
        if (statsController.isDead(owner) && brainStateMachine.isInState(CombatState.class)) {
            brainStateMachine.popState();
        } else if (isToFarAwayFromSpawn(owner)) {
            getBrain(owner).popState();
            returnToSpawnPoint(owner);
            // TODO remove this hack later
            MovePacket movePacket = new MovePacket(owner);
            hateListController.getHaters(owner).getPlayers()
                    .forEach(p -> packetSystem.sendPacket(p, movePacket));
        }
    }

    @Override
    public void exit(BrainStateMachine<Entity> brainStateMachine) {
        engine.event(HateListListener.class).remove(this);
    }

    private boolean isToFarAwayFromSpawn(Entity entity) {
        Position position = positionController.getPosition(entity);
        // TODO this should go into spawn manager
        Position spawnPosition = spawnCm.get(entity).getSpawnPosition();

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
        if (!statsController.isDead(entity)) {
            returnToSpawnPoint(entity);
        }
    }

//    private void startPatrol(Entity entity) {
//        returnToSpawnPoint(entity);
//        getBrain(entity).changeState(PatrolState.class);
//    }

    private void returnToSpawnPoint(Entity entity) {
        Position spawnPosition = spawnCm.get(entity).getSpawnPosition();
        positionController.moveEntity(entity, spawnPosition);
    }

    private BrainStateMachine<Entity> getBrain(Entity entity) {
        return brainHolderCm.get(entity).getBrainStateMachine();
    }
}
