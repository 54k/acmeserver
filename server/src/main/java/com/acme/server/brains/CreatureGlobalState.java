package com.acme.server.brains;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainHolder;
import com.acme.engine.mechanics.brains.BrainState;
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
public class CreatureGlobalState implements BrainState<Entity>, HateListListener {

    private ComponentMapper<BrainHolder> brainHolderCm;
    private ComponentMapper<Spawn> spawnCm;

    private Engine engine;

    private StatsController statsController;
    private PositionController positionController;
    private CombatController combatController;
    private HateListController hateListController;
    private PacketSystem packetSystem;

    private PatrolState patrolState;
    private CombatState combatState;

    public CreatureGlobalState() {
        patrolState = new PatrolState();
        combatState = new CombatState();
    }

    @Override
    public void enter(Entity entity) {
        changeState(entity, patrolState);
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        if (statsController.isDead(entity) && !hasState(entity, patrolState)) {
            changeState(entity, patrolState);
        } else if (isToFarAwayFromSpawn(entity)) {
            startPatrol(entity);
        }
    }

    private boolean isToFarAwayFromSpawn(Entity entity) {
        Position position = positionController.getPosition(entity);
        // TODO this should go into spawn manager
        Position spawnPosition = spawnCm.get(entity).getSpawnPosition();

        int distanceToFollow = 15;
        if (Math.abs(position.getX() - spawnPosition.getX()) >= distanceToFollow
                || Math.abs(position.getY() - spawnPosition.getY()) >= distanceToFollow) {
            startPatrol(entity);
            // TODO remove this hack later
            MovePacket movePacket = new MovePacket(entity);
            hateListController.getHaters(entity).getPlayers()
                    .forEach(p -> packetSystem.sendPacket(p, movePacket));
            return true;
        }
        return false;
    }

    @Override
    public void exit(Entity entity) {
        engine.event(HateListListener.class).remove(this);
    }

    @Override
    public void onHaterAdded(Entity entity, Entity hater) {
        changeState(entity, combatState);
    }

    @Override
    public void onHaterRemoved(Entity entity, Entity hater) {
    }

    @Override
    public void onHatersEmpty(Entity entity) {
        startPatrol(entity);
    }

    private void startPatrol(Entity entity) {
        System.out.println("Entity " + entity.getId() + " starts patroling");
        Position spawnPosition = spawnCm.get(entity).getSpawnPosition();
        positionController.moveEntity(entity, spawnPosition);
        changeState(entity, patrolState);
    }

    private boolean hasState(Entity entity, BrainState<Entity> brainState) {
        return brainHolderCm.get(entity).getBrain().isInState(brainState);
    }

    private void changeState(Entity entity, BrainState<Entity> brainState) {
        if (hasState(entity, brainState)) {
            return;
        }

        engine.processObject(brainState);
        BrainHolder brainHolder = brainHolderCm.get(entity);
        brainHolder.getBrain().changeState(brainState);
    }
}
