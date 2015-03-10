package com.acme.server.inventory;

import com.acme.ecs.core.*;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.commons.timer.SchedulerSystem;
import com.acme.server.combat.CombatListener;
import com.acme.server.entities.EntityFactory;
import com.acme.server.impacts.BlinkImpact;
import com.acme.server.inventory.LootTable.LootEntry;
import com.acme.server.managers.WorldTransform;
import com.acme.server.managers.WorldManager;
import com.acme.server.position.Transform;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

import java.util.List;
import java.util.stream.Collectors;

@Wire
public class LootTableSystem extends PassiveSystem implements CombatListener {

    private static final Aspect LOOT_TABLE_ASPECT = Aspect.all(LootTable.class).get();

    private ComponentMapper<WorldTransform> worldCm;
    private ComponentMapper<LootTable> lootTableCm;
    private ComponentMapper<Transform> transformCm;

    private Engine engine;
    private SchedulerSystem schedulerSystem;
    private EntityFactory entityFactory;
    private WorldManager worldManager;

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        if (LOOT_TABLE_ASPECT.matches(victim)) {
            dropItemsFrom(victim);
        }
    }

    public void dropItemsFrom(Entity entity) {
        List<LootEntry> lootEntries = getSucceedDrops(entity);

        WorldTransform worldTransform = worldCm.get(entity);
        Transform transform = transformCm.get(entity);
        Area dropArea = new Area(transform.getX() - 1, transform.getY() - 1, 2, 2);

        for (LootEntry lootEntry : lootEntries) {
            Entity dropEntity = entityFactory.createEntity(lootEntry.getType());
            worldCm.get(dropEntity).setInstance(worldTransform.getInstance());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            transformCm.get(dropEntity).setPosition(dropPosition);
            scheduleDecay(dropEntity);
            spawnDrop(dropEntity);
        }
    }

    private List<LootEntry> getSucceedDrops(Entity entity) {
        return lootTableCm.get(entity).getLootEntries().stream()
                .filter(this::isDropSucceed)
                .collect(Collectors.toList());
    }

    private boolean isDropSucceed(LootEntry lootEntry) {
        return lootEntry.getWeight() >= Rnd.between(0, 100);
    }

    private void scheduleDecay(Entity entity) {
        schedulerSystem.scheduleForEntity(entity, () -> entity.addComponent(new BlinkImpact()), 5000)
                .done(this::scheduleDecay0);
    }

    private void scheduleDecay0(Entity entity) {
        schedulerSystem.scheduleForEntity(entity, () -> {
            worldManager.decay(entity);
            worldManager.removeFromWorld(entity);
            engine.removeEntity(entity);
        }, 3000);
    }

    private void spawnDrop(Entity entity) {
        schedulerSystem.scheduleForEntity(entity, () -> {
            worldManager.bringIntoWorld(entity);
            worldManager.spawn(entity);
            return entity;
        }).done(this::scheduleDecay);
    }
}
