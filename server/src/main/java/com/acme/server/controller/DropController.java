package com.acme.server.controller;

import com.acme.commons.application.Context;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.DecayComponent;
import com.acme.server.component.DropComponent;
import com.acme.server.component.DropComponent.Drop;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.SpawnComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.event.CombatEvents;
import com.acme.server.manager.EntityManager;
import com.acme.server.manager.WorldManager;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import java.util.List;
import java.util.stream.Collectors;

@Wired
public class DropController extends ManagerSystem implements CombatEvents {

    private static final Family DROP_OWNERS_FAMILY = Family.all(DropComponent.class).get();

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
        if (DROP_OWNERS_FAMILY.matches(victim)) {
            dropItems(victim);
        }
    }

    public void dropItems(Entity entity) {
        List<DropComponent.Drop> drops = getSucceedDrops(entity);

        WorldComponent worldComponent = wcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        SpawnComponent spawnComponent = scm.get(entity);
        Area dropArea = new Area(positionComponent.getX() - 1, positionComponent.getY() - 1, 2, 2);

        for (DropComponent.Drop drop : drops) {
            Entity dropEntity = entityManager.createEntity(drop.getType());
            wcm.get(dropEntity).setInstance(worldComponent.getInstance());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            pcm.get(dropEntity).setPosition(dropPosition);
            DecayComponent decayComponent = new DecayComponent();
            decayComponent.setCooldown(spawnComponent.getCooldown());
            dropEntity.add(decayComponent);
            spawnDrop(dropEntity);
        }
    }

    private List<Drop> getSucceedDrops(Entity entity) {
        return dcm.get(entity).getDrops().stream()
                .filter(this::isDropSucceed)
                .collect(Collectors.toList());
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
