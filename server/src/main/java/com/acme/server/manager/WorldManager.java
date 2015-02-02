package com.acme.server.manager;

import com.acme.commons.application.Context;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.PopulationPacket;
import com.acme.server.system.GameServerNetworkSystem;
import com.acme.server.template.WorldTemplate;
import com.acme.server.util.EntityContainer;
import com.acme.server.world.Instance;
import com.acme.server.world.Region;
import com.acme.server.world.World;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Collection;
import java.util.Map;

@Wired
public class WorldManager extends ManagerSystem {

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<WorldComponent> wcm;

    private Context context;
    private GameServerNetworkSystem networkSystem;

    private final EntityContainer entities = new EntityContainer();
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
        addEntity(entity);
        broadcastPopulation();
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
        removeEntity(entity);
        broadcastPopulation();
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

    private void broadcastPopulation() {
        if (!entities.getPlayers().isEmpty()) {
            context.schedule(this::broadcastPopulation0);
        }
    }

    private void broadcastPopulation0() {
        world.getInstances().values().stream()
                .forEach(this::broadcastPopulationToInstance);
    }

    private void broadcastPopulationToInstance(Instance instance) {
        Collection<Entity> players = instance.getPlayers().values();
        int instanceCount = players.size();
        int worldCount = entities.getPlayers().size();
        PopulationPacket population = new PopulationPacket(instanceCount, worldCount);
        players.forEach(p -> networkSystem.sendPacket(p, population));
    }

    private void addEntity(Entity entity) {
        if (entities.contains(entity)) {
            throw new IllegalArgumentException("Duplicate entity id " + entity.getId());
        }
        entities.addEntity(entity);
    }

    private void removeEntity(Entity entity) {
        entities.removeEntity(entity);
    }

    public Map<Long, Entity> getPlayers() {
        return entities.getPlayers();
    }

    public Map<Long, Entity> getEntities() {
        return entities.getEntities();
    }

    public Entity findPlayerById(long id) {
        return entities.findPlayerById(id);
    }

    public Entity findEntityById(long id) {
        return entities.findEntityById(id);
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeFromWorld(entity);
    }
}
