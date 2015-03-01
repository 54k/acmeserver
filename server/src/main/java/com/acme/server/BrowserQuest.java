package com.acme.server;

import com.acme.engine.application.ApplicationAdapter;
import com.acme.engine.application.Context;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Processor;
import com.acme.engine.ecs.utils.reflection.ClassReflection;
import com.acme.engine.ecs.utils.reflection.Field;
import com.acme.engine.mechanics.network.NetworkServer;
import com.acme.engine.mechanics.timer.SchedulerSystem;
import com.acme.server.brains.CreatureBrainSystem;
import com.acme.server.combat.CombatSystem;
import com.acme.server.combat.HateListSystem;
import com.acme.server.combat.StatsSystem;
import com.acme.server.entities.EntityFactory;
import com.acme.server.entities.Type;
import com.acme.server.impacts.BlinkImpactSystem;
import com.acme.server.impacts.HealImpactSystem;
import com.acme.server.impacts.InvulImpactSystem;
import com.acme.server.impacts.RegenImpactSystem;
import com.acme.server.inventory.InventorySystem;
import com.acme.server.inventory.LootTableSystem;
import com.acme.server.inventory.PickupSystem;
import com.acme.server.managers.ChatManager;
import com.acme.server.managers.LoginManager;
import com.acme.server.managers.SpawnManager;
import com.acme.server.managers.WorldManager;
import com.acme.server.packets.PacketSystem;
import com.acme.server.position.KnownListSystem;
import com.acme.server.position.SpawnSystem;
import com.acme.server.position.TransformSystem;
import com.acme.server.templates.CreatureTemplate;
import com.acme.server.templates.WorldTemplate;
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
        engine.addProcessor(new ApplicationProcessor(context));
        PacketSystem packetSystem = new PacketSystem(objectMapper);
        engine.addSystem(packetSystem);

        engine.addSystem(new SchedulerSystem());
        engine.addSystem(new SpawnSystem());
        engine.addSystem(new CreatureBrainSystem());
        engine.addSystem(new KnownListSystem());

        engine.addSystem(new TransformSystem());
        engine.addSystem(new PickupSystem());
        engine.addSystem(new InventorySystem());
        engine.addSystem(new StatsSystem());
        engine.addSystem(new LootTableSystem());
        engine.addSystem(new CombatSystem());
        engine.addSystem(new HateListSystem());

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

    // TODO this should be deleted
    static class ApplicationProcessor implements Processor {

        private final Context context;

        public ApplicationProcessor(Context context) {
            this.context = context;
        }

        @Override
        public void processObject(Object object, Engine engine) {
            Class<?> objectClass = object.getClass();
            while (objectClass != null) {
                injectContext(object, objectClass);
                objectClass = objectClass.getSuperclass();
            }
        }

        private void injectContext(Object object, Class<?> objectClass) {
            Field[] fields = ClassReflection.getDeclaredFields(objectClass);
            for (Field field : fields) {
                if (Context.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    field.set(object, context);
                }
            }
        }
    }
}
