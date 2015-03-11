package com.acme.server.position;

import com.acme.commons.timer.TimerSystem;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.NodeFamily;
import com.acme.ecs.core.Wire;
import com.acme.server.combat.StatsSystem;
import com.acme.server.model.component.TransformComponent;
import com.acme.server.model.system.WorldSystem;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;

public class SpawnSystem extends TimerSystem<SpawnPoint> {

    //    private ComponentMapper<WorldComponent> worldCm;
    //    private ComponentMapper<Spawn> spawnCm;
    //    private ComponentMapper<Transform> transformCm;
    @Wire
    private NodeFamily<SpawnPointNode> worldMapper;
    @Wire
    private StatsSystem statsSystem;
    @Wire
    private WorldSystem worldSystem;

    public SpawnSystem() {
        super(SpawnPoint.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return !worldMapper.get(entity).getTransform().isSpawned();
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        if (StatsSystem.STATS_ASPECT.matches(entity)) {
            statsSystem.resetHitPoints(entity);
        }
        SpawnPointNode positionNode = worldMapper.get(entity);
        TransformComponent transform = positionNode.getTransform();
        int randomOrientation = Rnd.between(0, Orientation.values().length - 1);
        transform.setOrientation(Orientation.values()[randomOrientation]);
        SpawnPoint spawnPointComponent = positionNode.getSpawn();
        Instance instance = positionNode.getWorld().getInstance();
        Position spawnPosition = getRandomSpawnPosition(spawnPointComponent.getSpawnArea(), instance);
        spawnPointComponent.setLastSpawnPosition(spawnPosition);
        transform.setPosition(spawnPosition);
        spawnPointComponent.refreshTimer();
        worldSystem.spawn(entity);
    }

    private Position getRandomSpawnPosition(Area area, Instance instance) {
        Position spawnPosition;
        do {
            spawnPosition = PositionUtils.getRandomPositionInside(area);
        } while (instance.getWorld().isColliding(spawnPosition.getX(), spawnPosition.getY()));
        return spawnPosition;
    }
}
