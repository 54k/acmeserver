package com.acme.server.inventory;

import com.acme.commons.timer.SchedulerSystem;
import com.acme.ecs.core.*;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.combat.CombatListener;
import com.acme.server.entities.EntityFactory;
import com.acme.server.impacts.BlinkImpact;
import com.acme.server.inventory.LootTable.LootEntry;
import com.acme.server.model.component.PositionComponent;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.node.PositionNode;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.system.passive.PositionSystem;
import com.acme.server.model.system.passive.WorldSystem;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Position;

import java.util.List;
import java.util.stream.Collectors;

@Wire
public class LootTableSystem extends PassiveSystem implements CombatListener {

    private static final Aspect LOOT_TABLE_ASPECT = Aspect.all(LootTable.class).get();

    private ComponentMapper<WorldComponent> worldCm;
    private ComponentMapper<LootTable> lootTableCm;
    private ComponentMapper<PositionComponent> transformCm;

    private Engine engine;
    private SchedulerSystem schedulerSystem;
    private EntityFactory entityFactory;
    private WorldSystem worldSystem;
	private PositionSystem positionSystem;

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

        WorldComponent worldTransform = worldCm.get(entity);
        PositionComponent transform = transformCm.get(entity);
        Area dropArea = new Area(transform.position.getX() - 1, transform.position.getY() - 1, 2, 2);

        for (LootEntry lootEntry : lootEntries) {
            Entity dropEntity = entityFactory.createEntity(lootEntry.getType());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            transformCm.get(dropEntity).position.setPosition(dropPosition);
            scheduleDecay(dropEntity);
            spawnDrop(dropEntity, worldTransform.instance);
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
            positionSystem.decay(entity.getNode(PositionNode.class));
            worldSystem.removeFromWorld(entity.getNode(WorldNode.class));
            engine.removeEntity(entity);
        }, 3000);
    }

    private void spawnDrop(Entity entity, Instance instance) {
        schedulerSystem.scheduleForEntity(entity, () -> {
            worldSystem.addToWorld(entity.getNode(WorldNode.class), instance);
            positionSystem.spawn(entity.getNode(PositionNode.class));
            return entity;
        }).done(this::scheduleDecay);
    }
}
