package com.acme.server.brains;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainState;
import com.acme.engine.mechanics.brains.BrainStateMachine;
import com.acme.server.position.Spawn;
import com.acme.server.position.TransformSystem;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

@Wire
public class PatrolState implements BrainState<Entity> {

    private float interval;
    private float accumulator;

    private ComponentMapper<Spawn> spawnCm;
    private TransformSystem transformSystem;

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
        Area spawnArea = spawnCm.get(entity).getArea();
        Position rndPos = PositionUtils.getRandomPositionInside(spawnArea);
        transformSystem.moveEntity(entity, rndPos);
    }

    private void resetInterval() {
        interval = Rnd.between(1000, 4000);
    }

    @Override
    public void exit(BrainStateMachine<Entity> brainStateMachine) {
    }
}
