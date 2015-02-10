package com.acme.server.system;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.TimerSystem;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.SpawnComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.controller.StatsController;
import com.acme.server.manager.WorldManager;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class SpawnSystem extends TimerSystem<SpawnComponent> {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<SpawnComponent> scm;
    private ComponentMapper<PositionComponent> pcm;

    private StatsController statsController;

    private WorldManager worldManager;

    public SpawnSystem() {
        super(SpawnComponent.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return !pcm.get(entity).isSpawned();
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        if (StatsController.STATS_OWNERS_FAMILY.matches(entity)) {
            statsController.resetHitPoints(entity);
        }
        PositionComponent positionComponent = pcm.get(entity);
        int randomOrientation = Rnd.between(0, Orientation.values().length - 1);
        positionComponent.setOrientation(Orientation.values()[randomOrientation]);
        SpawnComponent spawnComponent = scm.get(entity);
        Position spawnPosition = getRandomSpawnPosition(spawnComponent.getArea(), wcm.get(entity).getInstance());
        spawnComponent.setSpawnPosition(spawnPosition);
        positionComponent.setPosition(spawnPosition);
        spawnComponent.refreshTimer();
        worldManager.spawn(entity);
    }

    private Position getRandomSpawnPosition(Area area, Instance instance) {
        Position spawnPosition;
        do {
            spawnPosition = PositionUtils.getRandomPositionInside(area);
        } while (instance.getWorld().isColliding(spawnPosition.getX(), spawnPosition.getY()));
        return spawnPosition;
    }
}
