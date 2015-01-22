package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.template.WorldTemplate;
import com.acme.server.world.Instance;
import com.acme.server.world.Region;
import com.acme.server.world.World;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class WorldManager extends ManagerSystem {

    @Wired
    private ComponentMapper<PositionComponent> pcm;
    @Wired
    private ComponentMapper<WorldComponent> wcm;

    private final World world;

    public WorldManager(WorldTemplate template) {
        this.world = new World(template);
    }

    public World getWorld() {
        return world;
    }

    public void bringIntoWorld(Entity entity) {
        WorldComponent worldComponent = wcm.get(entity);
        Instance instance = worldComponent.getInstance();
        instance.addEntity(entity);
    }

    public void spawn(Entity entity) {
        WorldComponent worldComponent = wcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        Instance instance = worldComponent.getInstance();
        Region newRegion = instance.findRegion(positionComponent.getPosition());
        newRegion.addEntity(entity);
        positionComponent.setRegion(newRegion);
        positionComponent.setSpawned(true);
    }

    public void removeFromWorld(Entity entity) {
        decay(entity);
        WorldComponent worldComponent = wcm.get(entity);
        Instance instance = worldComponent.getInstance();
        instance.removeEntity(entity);
    }

    public void decay(Entity entity) {
        PositionComponent positionComponent = pcm.get(entity);
        Region region = positionComponent.getRegion();
        region.removeEntity(entity);
        positionComponent.setSpawned(false);
    }

    public Instance getAvailableInstance() {
        return world.getInstances().values().stream().findFirst().get();
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeFromWorld(entity);
    }
}
