package com.acme.gameserver.manager;

import com.acme.core.application.Context;
import com.acme.core.ashley.ManagerSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.DropComponent;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.component.SpawnComponent;
import com.acme.gameserver.component.WorldComponent;
import com.acme.gameserver.entity.Type;
import com.acme.gameserver.template.RoamingAreaTemplate;
import com.acme.gameserver.template.StaticChestTemplate;
import com.acme.gameserver.world.Area;
import com.acme.gameserver.world.Instance;
import com.acme.gameserver.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.List;
import java.util.stream.Collectors;

@Wired
public class SpawnManager extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<DropComponent> dcm;

    private Context context;
    private EntityManager entityManager;
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
            Entity entity = entityManager.createEntity(roamingAreaTemplate.getType());
            WorldComponent worldComponent = wcm.get(entity);
            worldComponent.setInstance(instance);
            SpawnComponent spawnComponent = new SpawnComponent();
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
        Entity entity = entityManager.createEntity(type);
        WorldComponent worldComponent = wcm.get(entity);
        worldComponent.setInstance(instance);
        SpawnComponent spawnComponent = new SpawnComponent();
        spawnComponent.setArea(new Area(position.getX(), position.getY(), 0, 0));
        entity.add(spawnComponent);
        worldManager.bringIntoWorld(entity);
    }

    private void spawnStaticChests(Instance instance) {
        instance.getWorld().getTemplate().getStaticChests().forEach(ct -> spawnStaticChest(ct, instance));
    }

    private void spawnStaticChest(StaticChestTemplate ct, Instance instance) {
        Entity entity = entityManager.createEntity(Type.CHEST);
        WorldComponent worldComponent = wcm.get(entity);
        worldComponent.setInstance(instance);
        SpawnComponent spawnComponent = new SpawnComponent();
        spawnComponent.setArea(new Area(ct.getX(), ct.getY(), 0, 0));
        entity.add(spawnComponent);
        DropComponent dropComponent = dcm.get(entity);
        List<DropComponent.Drop> drops = ct.getI().stream()
                .map(i -> new DropComponent.Drop(Type.fromId(i), 100))
                .collect(Collectors.toList());
        dropComponent.getDrops().addAll(drops);
        worldManager.bringIntoWorld(entity);
    }
}
