package com.acme.server.position;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.timer.TimerSystem;
import com.acme.server.combat.StatsSystem;
import com.acme.server.managers.WorldComponent;
import com.acme.server.managers.WorldManager;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;

@Wire
public class SpawnSystem extends TimerSystem<Spawn> {

    private ComponentMapper<WorldComponent> worldCm;
    private ComponentMapper<Spawn> spawnCm;
    private ComponentMapper<Transform> transformCm;

    private StatsSystem statsSystem;
    private WorldManager worldManager;

    public SpawnSystem() {
        super(Spawn.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return !transformCm.get(entity).isSpawned();
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        if (StatsSystem.statsFamily.matches(entity)) {
            statsSystem.resetHitPoints(entity);
        }
        Transform transform = transformCm.get(entity);
        int randomOrientation = Rnd.between(0, Orientation.values().length - 1);
        transform.setOrientation(Orientation.values()[randomOrientation]);
        Spawn spawnComponent = spawnCm.get(entity);
        Position spawnPosition = getRandomSpawnPosition(spawnComponent.getArea(), worldCm.get(entity).getInstance());
        spawnComponent.setSpawnPosition(spawnPosition);
        transform.setPosition(spawnPosition);
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
