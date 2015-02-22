package com.acme.server;

import com.acme.engine.application.ApplicationAdapter;
import com.acme.engine.application.Context;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.mechanics.network.NetworkServer;
import com.acme.server.brain.CombatBrainState;
import com.acme.server.brain.PatrolBrainState;
import com.acme.server.combat.CombatController;
import com.acme.server.combat.HateListController;
import com.acme.server.combat.StatsController;
import com.acme.server.controller.PositionController;
import com.acme.server.entity.EntityFactory;
import com.acme.server.entity.Type;
import com.acme.server.impact.BlinkImpactSystem;
import com.acme.server.impact.HealImpactSystem;
import com.acme.server.impact.InvulImpactSystem;
import com.acme.server.impact.RegenImpactSystem;
import com.acme.server.inventory.DropListController;
import com.acme.server.inventory.InventoryController;
import com.acme.server.manager.ChatManager;
import com.acme.server.manager.LoginManager;
import com.acme.server.manager.SpawnManager;
import com.acme.server.manager.WorldManager;
import com.acme.server.pickup.PickupController;
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

    private ObjectMapper objectMapper;
    private NetworkServer networkServer;

    @Override
    public void create(Context context) {
        super.create(context);
        objectMapper = new ObjectMapper();
        Engine engine = getEngine();
        PacketSystem packetSystem = new PacketSystem(objectMapper);
        engine.addSystem(packetSystem);
        engine.addSystem(new SpawnSystem());
        engine.addSystem(new DecaySystem());
        engine.addSystem(new CreatureBrainSystem());
        engine.addSystem(new PatrolBrainState());
        engine.addSystem(new CombatBrainState());
        engine.addSystem(new KnownListSystem());

        engine.addSystem(new PositionController());
        engine.addSystem(new PickupController());
        engine.addSystem(new InventoryController());
        engine.addSystem(new StatsController());
        engine.addSystem(new DropListController());
        engine.addSystem(new CombatController());
        engine.addSystem(new HateListController());

        engine.addSystem(new RegenImpactSystem());
        engine.addSystem(new BlinkImpactSystem());
        engine.addSystem(new HealImpactSystem());
        engine.addSystem(new InvulImpactSystem());

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
        startNetworkServer(packetSystem);
        LOG.info("[Server started]");
    }

    private void populateWorld(SpawnManager spawnManager, WorldManager worldManager) {
        Instance instance = worldManager.getWorld().createInstance(100);
        spawnManager.spawnInstanceEntities(instance);
    }

    private void startNetworkServer(PacketSystem packetSystem) {
        networkServer = NetworkServer.create();
        networkServer.setListener(packetSystem);
        networkServer.bind(8000);
    }

    private WorldManager createWorldManager() {
        try {
            WorldTemplate template = objectMapper.readValue(getResourceAsStream("world.json"), WorldTemplate.class);
            return new WorldManager(template);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EntityFactory createEntityManager() {
        try {
            MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, Type.class, CreatureTemplate.class);
            Map<Type, CreatureTemplate> creaturesByType = objectMapper.readValue(getResourceAsStream("creatures.json"), mapType);
            return new EntityFactory(creaturesByType);
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
