package com.acme.server.brains;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.brains.BrainState;
import com.acme.server.component.Spawn;
import com.acme.server.controller.PositionController;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

@Wire
public class PatrolState implements BrainState<Entity> {

    private float interval;
    private float accumulator;

    private ComponentMapper<Spawn> spawnCm;
    private PositionController positionController;

    public PatrolState() {
        interval = Rnd.between(1000, 4000);
    }

    @Override
    public void enter(Entity entity) {
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        accumulator += deltaTime;
        if (accumulator >= interval) {
            accumulator -= interval;
            moveEntity(entity);
            interval = Rnd.between(1000, 4000);
        }
    }

    private void moveEntity(Entity entity) {
        Area spawnArea = spawnCm.get(entity).getArea();
        Position rndPos = PositionUtils.getRandomPositionInside(spawnArea);
        positionController.moveEntity(entity, rndPos);
    }

    @Override
    public void exit(Entity entity) {
    }
}
