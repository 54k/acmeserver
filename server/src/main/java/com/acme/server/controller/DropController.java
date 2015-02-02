package com.acme.server.controller;

import com.acme.commons.application.Context;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.*;
import com.acme.server.event.CombatEvents;
import com.acme.server.manager.EntityManager;
import com.acme.server.manager.WorldManager;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.util.TypeUtils;
import com.acme.server.world.Area;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.List;
import java.util.stream.Collectors;

@Wired
public class DropController extends ManagerSystem implements CombatEvents {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<DropComponent> dcm;
    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<SpawnComponent> scm;

    private Context context;

    private EntityManager entityManager;
    private WorldManager worldManager;

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        if (TypeUtils.isCreature(victim)) {
            dropItems(victim);
        }
    }

    public void dropItems(Entity entity) {
        List<DropComponent.Drop> drops = dcm.get(entity).getDrops().stream()
                .filter(this::isDropSucceed)
                .collect(Collectors.toList());

        WorldComponent worldComponent = wcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        SpawnComponent spawnComponent = scm.get(entity);
        Area dropArea = new Area(positionComponent.getX() - 1, positionComponent.getY() - 1, 2, 2);

        for (DropComponent.Drop drop : drops) {
            Entity dropEntity = entityManager.createEntity(drop.getType());
            wcm.get(dropEntity).setInstance(worldComponent.getInstance());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            pcm.get(dropEntity).setPosition(dropPosition);
            DespawnComponent despawnComponent = new DespawnComponent();
            despawnComponent.setCooldown(spawnComponent.getCooldown());
            dropEntity.add(despawnComponent);
            spawnDrop(dropEntity);
        }
    }

    private boolean isDropSucceed(DropComponent.Drop drop) {
        return drop.getChance() >= Rnd.between(0, 100);
    }

    private void spawnDrop(Entity dropEntity) {
        context.schedule(() -> {
            worldManager.bringIntoWorld(dropEntity);
            worldManager.spawn(dropEntity);
        });
    }
}
