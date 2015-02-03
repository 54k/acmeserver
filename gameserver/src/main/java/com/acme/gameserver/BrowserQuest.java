package com.acme.gameserver;

import com.acme.core.application.ApplicationAdapter;
import com.acme.core.application.Context;
import com.acme.core.ashley.EntityEngine;
import com.acme.core.network.NetworkServer;
import com.acme.gameserver.controller.*;
import com.acme.gameserver.entity.Type;
import com.acme.gameserver.manager.*;
import com.acme.gameserver.system.*;
import com.acme.gameserver.template.CreatureTemplate;
import com.acme.gameserver.template.WorldTemplate;
import com.acme.gameserver.util.WebSocketServer;
import com.acme.gameserver.world.Instance;
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
        GsPacketSystem gsPacketSystem = new GsPacketSystem();
        engine.addSystem(gsPacketSystem);
        engine.addSystem(new SpawnSystem());
        engine.addSystem(new DespawnSystem());
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
        startNetworkServer(gsPacketSystem);
        LOG.info("[Server started]");
    }

    private void populateWorld(SpawnManager spawnManager, WorldManager worldManager) {
        Instance instance = worldManager.getWorld().createInstance(100);
        spawnManager.spawnInstanceEntities(instance);
    }

    private void startNetworkServer(GsPacketSystem networkSystem) {
        networkServer = new WebSocketServer();
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
