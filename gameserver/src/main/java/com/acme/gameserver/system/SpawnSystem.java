package com.acme.gameserver.system;

import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.component.SpawnComponent;
import com.acme.gameserver.component.WorldComponent;
import com.acme.gameserver.manager.WorldManager;
import com.acme.gameserver.util.PositionUtils;
import com.acme.gameserver.world.Area;
import com.acme.gameserver.world.Instance;
import com.acme.gameserver.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

@Wired
public class SpawnSystem extends IteratingSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<SpawnComponent> scm;
    private ComponentMapper<PositionComponent> pcm;

    private WorldManager worldManager;

    public SpawnSystem() {
        super(Family.all(WorldComponent.class, SpawnComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        SpawnComponent spawnComponent = scm.get(entity);
        if (!positionComponent.isSpawned()) {
            float currentRespawnDelay = spawnComponent.getCooldown() - deltaTime;
            spawnComponent.setCooldown(currentRespawnDelay);

            if (spawnComponent.getCooldown() <= 0) {
                Position spawnPosition = getRandomSpawnPosition(spawnComponent.getArea(), wcm.get(entity).getInstance());
                spawnComponent.setSpawnPosition(spawnPosition);
                positionComponent.setPosition(spawnPosition);
                spawnComponent.refreshCooldown();
                worldManager.spawn(entity);
            }
        }
    }

    private Position getRandomSpawnPosition(Area area, Instance instance) {
        Position spawnPosition;
        do {
            spawnPosition = PositionUtils.getRandomPositionInside(area);
        } while (instance.getWorld().isColliding(spawnPosition.getX(), spawnPosition.getY()));
        return spawnPosition;
    }
}
