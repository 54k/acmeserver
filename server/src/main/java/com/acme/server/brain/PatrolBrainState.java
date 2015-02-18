package com.acme.server.brain;

import com.acme.engine.aegis.Wired;
import com.acme.engine.brain.BrainStateController;
import com.acme.server.component.PatrolComponent;
import com.acme.server.component.Spawn;
import com.acme.server.controller.PositionController;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PatrolBrainState extends BrainStateController {

    private ComponentMapper<PatrolComponent> pcm;
    private ComponentMapper<Spawn> scm;

    private PositionController positionController;

    @Override
    public void enter(Entity entity) {
        PatrolComponent patrolComponent = new PatrolComponent();
        patrolComponent.setCooldown(getPatrolCooldown());
        entity.add(patrolComponent);
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        PatrolComponent patrolComponent = pcm.get(entity);
        float cd = patrolComponent.getCooldown() - deltaTime;
        patrolComponent.setCooldown(cd);
        if (cd <= 0) {
            patrolComponent.setCooldown(getPatrolCooldown());
            moveCreature(entity);
        }
    }

    private static int getPatrolCooldown() {
        return Rnd.between(4000, 8000);
    }

    private void moveCreature(Entity entity) {
        Spawn spawnComponent = scm.get(entity);
        Area spawnArea = spawnComponent.getArea();
        Position rndPos = PositionUtils.getRandomPositionInside(spawnArea);
        positionController.moveEntity(entity, rndPos);
    }

    @Override
    public void exit(Entity entity) {
        entity.remove(PatrolComponent.class);
    }
}
