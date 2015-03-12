package com.acme.server.model.system.passive;

import com.acme.commons.network.SessionComponent;
import com.acme.commons.timer.SchedulerSystem;
import com.acme.commons.utils.collections.NodeList;
import com.acme.commons.utils.collections.Predicates;
import com.acme.ecs.core.*;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.event.WorldListener;
import com.acme.server.model.node.WorldNode;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.PopulationPacket;
import com.acme.server.templates.WorldTemplate;
import com.acme.server.world.Instance;
import com.acme.server.world.World;

import java.util.Collection;

public class WorldSystem extends PassiveSystem implements NodeListener {

    @Wire
    private SchedulerSystem schedulerSystem;
    @Wire
    private PacketSystem packetSystem;

    /**
     * all nodes in worlds
     */
    private final NodeList<WorldNode> worldNodes;
    private final World world;

    public WorldSystem(WorldTemplate template) {
        this.world = new World(template);
        worldNodes = new NodeList<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addNodeListener(WorldNode.class, this);
    }

    @Override
    public void nodeAdded(Node node) {
    }

    @Override
    public void nodeRemoved(Node node) {
        removeNode((WorldNode) node);
    }

    public World getWorld() {
        return world;
    }

    /**
     * Retrieves and returns first available instance
     *
     * @return available instance
     */
    public Instance getAvailableInstance() {
        return world.getInstances().values().stream().findFirst().get();
    }

    /**
     * Adds the given node to the world nodes and the given instance
     *
     * @param worldNode node
     * @param instance  instance
     * @throws java.lang.IllegalArgumentException if node contains in the world
     */
    public void addToWorld(WorldNode worldNode, Instance instance) {
        addWorldNode(worldNode);
        WorldComponent world = worldNode.getWorld();
        world.instance = instance;
        world.instance.addEntity(worldNode.getEntity());
        broadcastPopulation();
    }

    private void addWorldNode(WorldNode worldNode) {
        if (worldNodes.contains(worldNode)) {
            throw new IllegalArgumentException("Duplicate entity");
        }
        if (worldNodes.add(worldNode)) {
            event(WorldListener.class).dispatch().onWorldNodeAdded(worldNode);
        }
    }

    /**
     * Removes the given node from the world nodes
     *
     * @param worldNode node
     */
    public void removeFromWorld(WorldNode worldNode) {
        if (removeNode(worldNode)) {
            WorldComponent world = worldNode.getWorld();
            Instance instance = world.instance;
            instance.removeEntity(worldNode.getEntity());
            broadcastPopulation();
        }
    }

    private boolean removeNode(WorldNode worldNode) {
        if (worldNodes.remove(worldNode)) {
            event(WorldListener.class).dispatch().onWorldNodeRemoved(worldNode);
            return true;
        }
        return false;
    }

    private void broadcastPopulation() {
        if (!getPlayers().isEmpty()) {
            schedulerSystem.schedule(this::broadcastPopulation0);
        }
    }

    private void broadcastPopulation0() {
        world.getInstances().values().stream()
                .forEach(this::broadcastPopulationToInstance);
    }

    private void broadcastPopulationToInstance(Instance instance) {
        Collection<Entity> players = instance.getPlayers();
        int instanceCount = players.size();
        int worldCount = getPlayers().size();
        PopulationPacket population = new PopulationPacket(instanceCount, worldCount);
        players.forEach(p -> packetSystem.sendPacket(p, population));
    }

    /**
     * @return Copy of all players in the world
     */
    public NodeList<WorldNode> getPlayers() {
        return worldNodes.query(Predicates.aspect(Aspect.all(SessionComponent.class).get()));
    }

    /**
     * @return Copy of all nodes in the world
     */
    public NodeList<WorldNode> getWorldNodes() {
        return new NodeList<>(worldNodes);
    }

    public Entity getPlayerById(long id) {
        WorldNode worldNode = getPlayers().querySingle(Predicates.id(id));
        return worldNode != null ? worldNode.getEntity() : null;
    }

    public Entity getEntityById(long id) {
        WorldNode worldNode = worldNodes.querySingle(Predicates.id(id));
        return worldNode != null ? worldNode.getEntity() : null;
    }
}
