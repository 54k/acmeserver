package com.acme.server.brains;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.commons.brains.BrainState;
import com.acme.commons.brains.BrainStateMachine;
import com.acme.server.position.MoveSystem;
import com.acme.server.position.SpawnPoint;
import com.acme.server.position.TransformNode;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

@Wire
public class PatrolState implements BrainState<Entity> {

    private float interval;
    private float accumulator;

    private ComponentMapper<SpawnPoint> spawnCm;
    private MoveSystem moveSystem;

    public PatrolState() {
        resetInterval();
    }

    @Override
    public void enter(BrainStateMachine<Entity> brainStateMachine) {
    }

    @Override
    public void update(BrainStateMachine<Entity> brainStateMachine, float deltaTime) {
        accumulator += deltaTime;
        if (accumulator >= interval) {
            accumulator -= interval;
            moveEntity(brainStateMachine.getOwner());
            resetInterval();
        }
    }

    private void moveEntity(Entity entity) {
        Area spawnArea = spawnCm.get(entity).getSpawnArea();
        Position rndPos = PositionUtils.getRandomPositionInside(spawnArea);
        moveSystem.moveTo(entity.getNode(TransformNode.class), rndPos);
    }

    private void resetInterval() {
        interval = Rnd.between(1000, 4000);
    }

    @Override
    public void exit(BrainStateMachine<Entity> brainStateMachine) {
    }
}
