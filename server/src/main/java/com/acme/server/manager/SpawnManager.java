package com.acme.server.manager;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.aegis.systems.PassiveSystem;
import com.acme.engine.application.Context;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.Spawn;
import com.acme.server.component.WorldComponent;
import com.acme.server.entity.EntityFactory;
import com.acme.server.entity.Type;
import com.acme.server.inventory.DropList;
import com.acme.server.template.RoamingAreaTemplate;
import com.acme.server.template.StaticChestTemplate;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Position;

import java.util.List;
import java.util.stream.Collectors;

@Wired
public class SpawnManager extends PassiveSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<DropList> dcm;

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
            WorldComponent worldComponent = wcm.get(entity);
            worldComponent.setInstance(instance);
            Spawn spawnComponent = new Spawn();
            spawnComponent.setArea(new Area(roamingAreaTemplate.getX(),
                    roamingAreaTemplate.getY(),
                    roamingAreaTemplate.getWidth(),
                    roamingAreaTemplate.getHeight()));
            entity.add(spawnComponent);
            worldManager.bringIntoWorld(entity);
        }
    }

    private void spawnStaticObjects(Instance instance) {
        instance.getWorld().getStaticObjectSpawns()
                .forEach((k, v) -> spawnStaticObject(instance, k, v));
    }

    private void spawnStaticObject(Instance instance, Position position, Type type) {
        Entity entity = entityFactory.createEntity(type);
        WorldComponent worldComponent = wcm.get(entity);
        worldComponent.setInstance(instance);
        Spawn spawnComponent = new Spawn();
        spawnComponent.setArea(new Area(position.getX(), position.getY(), 0, 0));
        entity.add(spawnComponent);
        worldManager.bringIntoWorld(entity);
    }

    private void spawnStaticChests(Instance instance) {
        instance.getWorld().getTemplate().getStaticChests().forEach(ct -> spawnStaticChest(ct, instance));
    }

    private void spawnStaticChest(StaticChestTemplate ct, Instance instance) {
        Entity entity = entityFactory.createEntity(Type.CHEST);
        WorldComponent worldComponent = wcm.get(entity);
        worldComponent.setInstance(instance);
        Spawn spawnComponent = new Spawn();
        spawnComponent.setArea(new Area(ct.getX(), ct.getY(), 0, 0));
        entity.add(spawnComponent);
        DropList dropList = dcm.get(entity);
        List<DropList.Drop> drops = ct.getI().stream()
                .map(i -> new DropList.Drop(Type.fromId(i), 100))
                .collect(Collectors.toList());
        dropList.getDrops().addAll(drops);
        worldManager.bringIntoWorld(entity);
    }
}