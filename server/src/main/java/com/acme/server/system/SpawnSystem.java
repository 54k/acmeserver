package com.acme.server.system;

import com.acme.engine.processors.Wired;
import com.acme.engine.timer.TimerSystem;
import com.acme.server.combat.StatsController;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.Spawn;
import com.acme.server.component.WorldComponent;
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
public class SpawnSystem extends TimerSystem<Spawn> {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<Spawn> scm;
    private ComponentMapper<PositionComponent> pcm;

    private StatsController statsController;

    private WorldManager worldManager;

    public SpawnSystem() {
        super(Spawn.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return !pcm.get(entity).isSpawned();
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        if (StatsController.statsFamily.matches(entity)) {
            statsController.resetHitPoints(entity);
        }
        PositionComponent positionComponent = pcm.get(entity);
        int randomOrientation = Rnd.between(0, Orientation.values().length - 1);
        positionComponent.setOrientation(Orientation.values()[randomOrientation]);
        Spawn spawnComponent = scm.get(entity);
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
