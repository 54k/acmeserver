package com.acme.server.manager;

import com.acme.engine.application.Context;
import com.acme.engine.systems.ManagerSystem;
import com.acme.engine.aegis.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.event.WorldManagerEvent;
import com.acme.server.packet.outbound.PopulationPacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.template.WorldTemplate;
import com.acme.server.util.EntityContainer;
import com.acme.server.world.Instance;
import com.acme.server.world.Region;
import com.acme.server.world.World;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import java.util.Collection;

@Wired
public class WorldManager extends ManagerSystem {

    private static final Family worldEntitiesFamily = Family.all(PositionComponent.class, WorldComponent.class).get();

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<WorldComponent> wcm;

    private Context context;
    private PacketSystem packetSystem;

    private final EntityContainer entities = new EntityContainer();
    private final World world;

    public WorldManager(WorldTemplate template) {
        super(worldEntitiesFamily);
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
        post(WorldManagerEvent.class).onEntitySpawned(entity);
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
        post(WorldManagerEvent.class).onEntityDecayed(entity);
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
        Collection<Entity> players = instance.getPlayers();
        int instanceCount = players.size();
        int worldCount = entities.getPlayers().size();
        PopulationPacket population = new PopulationPacket(instanceCount, worldCount);
        players.forEach(p -> packetSystem.sendPacket(p, population));
    }

    private void addEntity(Entity entity) {
        if (entities.containsById(entity.getId())) {
            throw new IllegalArgumentException("Duplicate entity id " + entity.getId());
        }
        entities.add(entity);
    }

    private void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public EntityContainer getPlayers() {
        return entities.getPlayers();
    }

    public EntityContainer getEntities() {
        return entities;
    }

    public Entity getPlayerById(long id) {
        return entities.getPlayerById(id).orElse(null);
    }

    public Entity getEntityById(long id) {
        return entities.getEntityById(id).orElse(null);
    }

    @Override
    public void entityRemoved0(Entity entity) {
        removeFromWorld(entity);
    }
}
