package com.acme.server.ai;

import com.acme.commons.ai.BrainState;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.PatrolComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.SpawnComponent;
import com.acme.server.manager.PositionManager;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PatrolState extends ManagerSystem implements BrainState {

    private ComponentMapper<PatrolComponent> pcm;
    private ComponentMapper<SpawnComponent> scm;
    private ComponentMapper<PositionComponent> poscm;

    private PositionManager positionManager;

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
        positionManager.moveEntity(entity, rndPos);
    }

    @Override
    public void exit(Entity entity) {
        entity.remove(PatrolComponent.class);
    }
}
