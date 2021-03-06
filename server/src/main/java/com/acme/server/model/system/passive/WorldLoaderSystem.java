package com.acme.server.model.system.passive;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.entities.EntityFactory;
import com.acme.server.entities.Type;
import com.acme.server.inventory.LootTable;
import com.acme.server.model.component.PositionComponent;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.component.SpawnComponent;
import com.acme.server.templates.RoamingAreaTemplate;
import com.acme.server.templates.StaticChestTemplate;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Position;
import com.acme.server.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class WorldLoaderSystem extends PassiveSystem {

	@Wire
	private ComponentMapper<WorldComponent> worldCm;
	@Wire
	private ComponentMapper<PositionComponent> transformCm;
	@Wire
	private ComponentMapper<LootTable> lootTableCm;

	@Wire
	private EntityFactory entityFactory;
	@Wire
	private WorldSystem worldSystem;

	public void loadWorldEntities(World world) {
		world.getInstances().values().forEach(this::loadInstanceEntities);
	}

	public void loadInstanceEntities(Instance instance) {
		spawnCreatures(instance);
		spawnStaticObjects(instance);
		spawnStaticChests(instance);
	}

	private void spawnCreatures(Instance instance) {
		List<RoamingAreaTemplate> areas = instance.getWorld().getTemplate().getRoamingAreas();
		areas.forEach(area -> spawnCreatures(instance, area));
	}

	private void spawnCreatures(Instance instance, RoamingAreaTemplate roamingAreaTemplate) {
		for (int i = 0; i < roamingAreaTemplate.getNb(); i++) {
			Entity entity = entityFactory.createEntity(roamingAreaTemplate.getType());
			SpawnComponent spawnComponent = new SpawnComponent();
			spawnComponent.area = new Area(roamingAreaTemplate.getX(),
					roamingAreaTemplate.getY(),
					roamingAreaTemplate.getWidth(),
					roamingAreaTemplate.getHeight());
			entity.addComponent(spawnComponent);
			worldSystem.addToWorld(entity.getNode(WorldNode.class), instance);
		}
	}

	private void spawnStaticObjects(Instance instance) {
		instance.getWorld().getStaticObjectSpawns()
				.forEach((k, v) -> spawnStaticObject(instance, k, v));
	}

	private void spawnStaticObject(Instance instance, Position position, Type type) {
		Entity entity = entityFactory.createEntity(type);
		SpawnComponent spawnComponent = new SpawnComponent();
		spawnComponent.area = new Area(position.getX(), position.getY(), 0, 0);
		entity.addComponent(spawnComponent);
		worldSystem.addToWorld(entity.getNode(WorldNode.class), instance);
	}

	private void spawnStaticChests(Instance instance) {
		instance.getWorld().getTemplate().getStaticChests().forEach(ct -> spawnStaticChest(ct, instance));
	}

	private void spawnStaticChest(StaticChestTemplate ct, Instance instance) {
		Entity entity = entityFactory.createEntity(Type.CHEST);
		SpawnComponent spawnComponent = new SpawnComponent();
		spawnComponent.area = new Area(ct.getX(), ct.getY(), 0, 0);
		entity.addComponent(spawnComponent);
		LootTable lootTable = lootTableCm.get(entity);
		List<LootTable.LootEntry> lootEntries = ct.getI().stream()
				.map(i -> new LootTable.LootEntry(Type.fromId(i), 100))
				.collect(Collectors.toList());
		lootTable.getLootEntries().addAll(lootEntries);
		worldSystem.addToWorld(entity.getNode(WorldNode.class), instance);
	}
}