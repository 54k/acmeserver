package com.acme.server.managers;

import com.acme.engine.application.Context;
import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.entities.EntityFactory;
import com.acme.server.entities.Type;
import com.acme.server.inventory.LootTable;
import com.acme.server.position.Spawn;
import com.acme.server.position.Transform;
import com.acme.server.templates.RoamingAreaTemplate;
import com.acme.server.templates.StaticChestTemplate;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Position;

import java.util.List;
import java.util.stream.Collectors;

@Wire
public class SpawnManager extends PassiveSystem {

    private ComponentMapper<WorldComponent> worldCm;
    private ComponentMapper<Transform> transformCm;
    private ComponentMapper<LootTable> lootTableCm;

    private Context context;
    private EntityFactory entityFactory;
    private WorldManager worldManager;

    public void spawnInstanceEntities(Instance instance) {
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
            WorldComponent worldComponent = worldCm.get(entity);
            worldComponent.setInstance(instance);
            Spawn spawnComponent = new Spawn();
            spawnComponent.setArea(new Area(roamingAreaTemplate.getX(),
                    roamingAreaTemplate.getY(),
                    roamingAreaTemplate.getWidth(),
                    roamingAreaTemplate.getHeight()));
            entity.addComponent(spawnComponent);
            worldManager.bringIntoWorld(entity);
        }
    }

    private void spawnStaticObjects(Instance instance) {
        instance.getWorld().getStaticObjectSpawns()
                .forEach((k, v) -> spawnStaticObject(instance, k, v));
    }

    private void spawnStaticObject(Instance instance, Position position, Type type) {
        Entity entity = entityFactory.createEntity(type);
        WorldComponent worldComponent = worldCm.get(entity);
        worldComponent.setInstance(instance);
        Spawn spawnComponent = new Spawn();
        spawnComponent.setArea(new Area(position.getX(), position.getY(), 0, 0));
        entity.addComponent(spawnComponent);
        worldManager.bringIntoWorld(entity);
    }

    private void spawnStaticChests(Instance instance) {
        instance.getWorld().getTemplate().getStaticChests().forEach(ct -> spawnStaticChest(ct, instance));
    }

    private void spawnStaticChest(StaticChestTemplate ct, Instance instance) {
        Entity entity = entityFactory.createEntity(Type.CHEST);
        WorldComponent worldComponent = worldCm.get(entity);
        worldComponent.setInstance(instance);
        Spawn spawnComponent = new Spawn();
        spawnComponent.setArea(new Area(ct.getX(), ct.getY(), 0, 0));
        entity.addComponent(spawnComponent);
        LootTable lootTable = lootTableCm.get(entity);
        List<LootTable.LootEntry> lootEntries = ct.getI().stream()
                .map(i -> new LootTable.LootEntry(Type.fromId(i), 100))
                .collect(Collectors.toList());
        lootTable.getLootEntries().addAll(lootEntries);
        worldManager.bringIntoWorld(entity);
    }
}