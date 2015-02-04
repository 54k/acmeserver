package com.acme.server;

import com.acme.commons.application.ApplicationAdapter;
import com.acme.commons.application.Context;
import com.acme.commons.ashley.EntityEngine;
import com.acme.commons.network.NetworkServer;
import com.acme.server.controller.*;
import com.acme.server.entity.Type;
import com.acme.server.manager.*;
import com.acme.server.system.*;
import com.acme.server.template.CreatureTemplate;
import com.acme.server.template.WorldTemplate;
import com.acme.server.world.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserQuest extends ApplicationAdapter {

    private static final Logger LOG = Logger.getAnonymousLogger();

    private NetworkServer networkServer;

    @Override
    public void create(Context context) {
        super.create(context);
        context.register(ObjectMapper.class, new ObjectMapper());
        EntityEngine engine = context.get(EntityEngine.class);
        PacketSystem networkSystem = new PacketSystem();
        engine.addSystem(networkSystem);
        engine.addSystem(new SpawnSystem());
        engine.addSystem(new DecaySystem());
        engine.addSystem(new InvulnerabilitySystem());
        engine.addSystem(new CreatureBrainSystem());
        engine.addSystem(new KnownListSystem());

        engine.addSystem(new PositionController());
        engine.addSystem(new PickupController());
        engine.addSystem(new InventoryController());
        engine.addSystem(new StatsController());
        engine.addSystem(new DropController());
        engine.addSystem(new CombatController());
        engine.addSystem(new HateController());

        WorldManager worldManager = createWorldManager();
        engine.addSystem(worldManager);
        engine.addSystem(createEntityManager());
        SpawnManager spawnManager = new SpawnManager();
        engine.addSystem(spawnManager);
        engine.addSystem(new LoginManager());
        engine.addSystem(new ChatManager());

        engine.initialize();
        LOG.info("[Engine initialized]");

        populateWorld(spawnManager, worldManager);
        LOG.info("[World created]");
        startNetworkServer(networkSystem);
        LOG.info("[Server started]");
    }

    private void populateWorld(SpawnManager spawnManager, WorldManager worldManager) {
        Instance instance = worldManager.getWorld().createInstance(100);
        spawnManager.spawnInstanceEntities(instance);
    }

    private void startNetworkServer(PacketSystem networkSystem) {
        networkServer = NetworkServer.create();
        networkServer.setListener(networkSystem);
        networkServer.bind(8000);
    }

    private WorldManager createWorldManager() {
        try {
            ObjectMapper objectMapper = getContext().get(ObjectMapper.class);
            WorldTemplate template = objectMapper.readValue(getResourceAsStream("world.json"), WorldTemplate.class);
            return new WorldManager(template);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EntityManager createEntityManager() {
        try {
            ObjectMapper objectMapper = getContext().get(ObjectMapper.class);
            MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, Type.class, CreatureTemplate.class);
            Map<Type, CreatureTemplate> creaturesByType = objectMapper.readValue(getResourceAsStream("creatures.json"), mapType);
            return new EntityManager(creaturesByType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    @Override
    public void update() {
        networkServer.update();
        super.update();
    }

    @Override
    public void dispose() {
        super.dispose();
        networkServer.dispose();
    }

    @Override
    public void handleError(Throwable t) {
        LOG.log(Level.SEVERE, t.getMessage(), t);
    }
}
