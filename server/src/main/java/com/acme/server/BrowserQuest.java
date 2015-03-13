package com.acme.server;

import com.acme.commons.application.ApplicationAdapter;
import com.acme.commons.application.Context;
import com.acme.commons.network.NetworkServer;
import com.acme.commons.timer.SchedulerSystem;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Processor;
import com.acme.ecs.utils.reflection.ClassReflection;
import com.acme.ecs.utils.reflection.Field;
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
import com.acme.server.model.system.passive.WorldSpawnerSystem;
import com.acme.server.model.system.active.KnownListSystem;
import com.acme.server.model.system.active.PositionSystem;
import com.acme.server.model.system.passive.WorldSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.model.system.active.SpawnSystem;
import com.acme.server.templates.CreatureTemplate;
import com.acme.server.templates.WorldTemplate;
import com.acme.server.world.World;
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

		engine.addSystem(new PositionSystem());
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

		WorldSystem worldSystem = createWorldManager();
		engine.addSystem(worldSystem);
		engine.addSystem(createEntityManager());
		WorldSpawnerSystem worldSpawnerSystem = new WorldSpawnerSystem();
		engine.addSystem(worldSpawnerSystem);
		engine.addSystem(new LoginManager());
		engine.addSystem(new ChatManager());

		engine.initialize();
		LOG.info("[Engine initialized]");

		populateWorld(worldSpawnerSystem, worldSystem);
		LOG.info("[World created]");
		startNetworkServer(packetSystem);
		LOG.info("[Server started]");
	}

	private void populateWorld(WorldSpawnerSystem worldSpawnerSystem, WorldSystem worldSystem) {
		World world = worldSystem.getWorld();
		world.createInstance(100);
		worldSpawnerSystem.spawnWorldEntities(world);
	}

	private void startNetworkServer(PacketSystem packetSystem) {
		networkServer = NetworkServer.create();
		networkServer.setListener(packetSystem);
		networkServer.bind(8000);
	}

	private WorldSystem createWorldManager() {
		try {
			WorldTemplate template = objectMapper.readValue(getResourceAsStream("world.json"), WorldTemplate.class);
			return new WorldSystem(template);
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
