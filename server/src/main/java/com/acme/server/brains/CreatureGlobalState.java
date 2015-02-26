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

    @Override
    public void enter(Entity entity) {
        getBrain(entity).changeState(PatrolState.class);
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        if (statsController.isDead(entity) && !isInState(entity, PatrolState.class)) {
            getBrain(entity).changeState(PatrolState.class);
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
        if (!isInState(entity, CombatState.class)) {
            getBrain(entity).changeState(CombatState.class);
        }
    }

    @Override
    public void onHaterRemoved(Entity entity, Entity hater) {
    }

    @Override
    public void onHatersEmpty(Entity entity) {
        if (statsController.isDead(entity)) {
            getBrain(entity).changeState(PatrolState.class);
        } else {
            startPatrol(entity);
        }
    }

    private void startPatrol(Entity entity) {
        Position spawnPosition = spawnCm.get(entity).getSpawnPosition();
        positionController.moveEntity(entity, spawnPosition);
        getBrain(entity).changeState(PatrolState.class);
    }

    private BrainStateMachine getBrain(Entity entity) {
        return (BrainStateMachine) brainHolderCm.get(entity).getBrainStateMachine();
    }

    private boolean isInState(Entity entity, Class<? extends BrainState<Entity>> brainStateClass) {
        return getBrain(entity).isInState(brainStateClass);
    }
}
