package com.acme.server.system;

import com.acme.commons.ashley.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.SpawnComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.manager.WorldManager;
import com.acme.server.util.PositionUtils;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Position;
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
        //noinspection unchecked
        super(Family.all(WorldComponent.class, SpawnComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        SpawnComponent spawnComponent = scm.get(entity);
        if (!positionComponent.isSpawned()) {
            int currentRespawnDelay = (int) (spawnComponent.getCooldown() - deltaTime);
            spawnComponent.setCooldown(currentRespawnDelay);

            if (spawnComponent.getCooldown() <= 0) {
                positionComponent.setPosition(getRandomSpawnPosition(spawnComponent.getArea(), wcm.get(entity).getInstance()));
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
