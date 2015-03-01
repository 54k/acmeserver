package com.acme.server.inventory;

import com.acme.engine.application.Context;
import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.combat.CombatListener;
import com.acme.server.entities.EntityFactory;
import com.acme.server.inventory.LootTable.LootEntry;
import com.acme.server.managers.WorldComponent;
import com.acme.server.managers.WorldManager;
import com.acme.server.position.Spawn;
import com.acme.server.position.Transform;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

import java.util.List;
import java.util.stream.Collectors;

@Wire
public class LootTablesSystem extends PassiveSystem implements CombatListener {

    private static final Family dropListFamily = Family.all(LootTable.class).get();

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<LootTable> dropListCm;
    private ComponentMapper<Transform> pcm;
    private ComponentMapper<Spawn> scm;

    private Context context;

    private EntityFactory entityFactory;
    private WorldManager worldManager;

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        if (dropListFamily.matches(victim)) {
            dropItemsFrom(victim);
        }
    }

    public void dropItemsFrom(Entity entity) {
        List<LootEntry> lootEntries = getSucceedDrops(entity);

        WorldComponent worldComponent = wcm.get(entity);
        Transform transform = pcm.get(entity);
        Spawn spawnComponent = scm.get(entity);
        Area dropArea = new Area(transform.getX() - 1, transform.getY() - 1, 2, 2);

        for (LootEntry lootEntry : lootEntries) {
            Entity dropEntity = entityFactory.createEntity(lootEntry.getType());
            wcm.get(dropEntity).setInstance(worldComponent.getInstance());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            pcm.get(dropEntity).setPosition(dropPosition);
            LootDecay lootDecayComponent = new LootDecay();
            lootDecayComponent.setTime(spawnComponent.getTime());
            dropEntity.add(lootDecayComponent);
            spawnDrop(dropEntity);
        }
    }

    private List<LootEntry> getSucceedDrops(Entity entity) {
        return dropListCm.get(entity).getLootEntries().stream()
                .filter(this::isDropSucceed)
                .collect(Collectors.toList());
    }

    private boolean isDropSucceed(LootEntry lootEntry) {
        return lootEntry.getWeight() >= Rnd.between(0, 100);
    }

    private void spawnDrop(Entity dropEntity) {
        context.schedule(() -> {
            worldManager.bringIntoWorld(dropEntity);
            worldManager.spawn(dropEntity);
        });
    }
}
