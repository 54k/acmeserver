package com.acme.gameserver.controller;

import com.acme.core.application.Context;
import com.acme.core.ashley.ManagerSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.*;
import com.acme.gameserver.event.CombatEvents;
import com.acme.gameserver.manager.EntityManager;
import com.acme.gameserver.manager.WorldManager;
import com.acme.gameserver.util.PositionUtils;
import com.acme.gameserver.util.Rnd;
import com.acme.gameserver.util.TypeUtils;
import com.acme.gameserver.world.Area;
import com.acme.gameserver.world.Position;
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
