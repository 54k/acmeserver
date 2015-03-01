package com.acme.server.managers;

import com.acme.engine.application.Context;
import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.PopulationPacket;
import com.acme.server.position.Transform;
import com.acme.server.templates.WorldTemplate;
import com.acme.server.utils.EntityContainer;
import com.acme.server.world.Instance;
import com.acme.server.world.Region;
import com.acme.server.world.World;

import java.util.Collection;

@Wire
public class WorldManager extends PassiveSystem {

    private static final Family worldEntitiesFamily = Family.all(Transform.class, WorldComponent.class).get();

    private ComponentMapper<Transform> pcm;
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
        Transform transform = pcm.get(entity);
        Instance instance = worldComponent.getInstance();
        Region newRegion = instance.findRegion(transform.getPosition());
        newRegion.addEntity(entity);
        transform.setRegion(newRegion);
        transform.setSpawned(true);
        event(WorldManagerEventListener.class).dispatch().onEntitySpawned(entity);
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
        Transform transform = pcm.get(entity);
        Region region = transform.getRegion();
        region.removeEntity(entity);
        transform.setSpawned(false);
        event(WorldManagerEventListener.class).dispatch().onEntityDecayed(entity);
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
        if (entities.containsId(entity.getId())) {
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
        return entities.getPlayer(id).orElse(null);
    }

    public Entity getEntityById(long id) {
        return entities.getEntity(id).orElse(null);
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeFromWorld(entity);
    }
}
