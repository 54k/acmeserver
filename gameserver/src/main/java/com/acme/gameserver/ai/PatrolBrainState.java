package com.acme.gameserver.ai;

import com.acme.core.ai.BrainState;
import com.acme.core.ai.BrainStateController;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.PatrolComponent;
import com.acme.gameserver.component.SpawnComponent;
import com.acme.gameserver.controller.PositionController;
import com.acme.gameserver.util.PositionUtils;
import com.acme.gameserver.util.Rnd;
import com.acme.gameserver.world.Area;
import com.acme.gameserver.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PatrolBrainState extends BrainStateController implements BrainState {

    private ComponentMapper<PatrolComponent> pcm;
    private ComponentMapper<SpawnComponent> scm;

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
        SpawnComponent spawnComponent = scm.get(entity);
        Area spawnArea = spawnComponent.getArea();
        Position rndPos = PositionUtils.getRandomPositionInside(spawnArea);
        positionController.moveEntity(entity, rndPos);
    }

    @Override
    public void exit(Entity entity) {
        entity.remove(PatrolComponent.class);
    }
}
