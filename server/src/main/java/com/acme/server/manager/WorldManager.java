package com.acme.server.manager;

import com.acme.commons.application.Context;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.PopulationPacket;
import com.acme.server.system.NetworkSystem;
import com.acme.server.template.WorldTemplate;
import com.acme.server.world.Instance;
import com.acme.server.world.Region;
import com.acme.server.world.World;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Wired
public class WorldManager extends ManagerSystem {

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<WorldComponent> wcm;

    private Context context;
    private NetworkSystem networkSystem;

    private final World world;

    private final Map<Long, Entity> entitiesById = new HashMap<>();
    private final Map<Long, Entity> playersById = new HashMap<>();

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
        if (!playersById.isEmpty()) {
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
        int worldCount = playersById.size();
        PopulationPacket population = new PopulationPacket(instanceCount, worldCount);
        players.forEach(p -> networkSystem.sendPacket(p, population));
    }

    private void addEntity(Entity entity) {
        if (entitiesById.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Duplicate entity id " + entity.getId());
        }

        if (EntityManager.isPlayer(entity)) {
            addPlayer(entity);
        }
        entitiesById.put(entity.getId(), entity);
    }

    private void removeEntity(Entity entity) {
        if (EntityManager.isPlayer(entity)) {
            removePlayer(entity);
        }
        entitiesById.remove(entity.getId(), entity);
    }

    public Entity findEntity(long id) {
        return entitiesById.get(id);
    }

    public Map<Long, Entity> getEntities() {
        return entitiesById;
    }

    private void addPlayer(Entity player) {
        playersById.put(player.getId(), player);
        broadcastPopulation();
    }

    private void removePlayer(Entity player) {
        playersById.remove(player.getId());
        broadcastPopulation();
    }

    public Map<Long, Entity> getPlayers() {
        return playersById;
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeFromWorld(entity);
    }
}
