package com.acme.server.inventory;

import com.acme.engine.application.Context;
import com.acme.engine.systems.ManagerSystem;
import com.acme.engine.aegis.Wired;
import com.acme.server.combat.CombatListener;
import com.acme.server.component.Decay;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.Spawn;
import com.acme.server.component.WorldComponent;
import com.acme.server.entity.EntityFactory;
import com.acme.server.inventory.DropList.Drop;
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
public class DropListController extends ManagerSystem implements CombatListener {

    private static final Family dropListFamily = Family.all(DropList.class).get();

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<DropList> dropListCm;
    private ComponentMapper<PositionComponent> pcm;
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
        List<DropList.Drop> drops = getSucceedDrops(entity);

        WorldComponent worldComponent = wcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        Spawn spawnComponent = scm.get(entity);
        Area dropArea = new Area(positionComponent.getX() - 1, positionComponent.getY() - 1, 2, 2);

        for (DropList.Drop drop : drops) {
            Entity dropEntity = entityFactory.createEntity(drop.getType());
            wcm.get(dropEntity).setInstance(worldComponent.getInstance());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            pcm.get(dropEntity).setPosition(dropPosition);
            Decay decayComponent = new Decay();
            decayComponent.setTime(spawnComponent.getTime());
            dropEntity.add(decayComponent);
            spawnDrop(dropEntity);
        }
    }

    private List<Drop> getSucceedDrops(Entity entity) {
        return dropListCm.get(entity).getDrops().stream()
                .filter(this::isDropSucceed)
                .collect(Collectors.toList());
    }

    private boolean isDropSucceed(DropList.Drop drop) {
        return drop.getChance() >= Rnd.between(0, 100);
    }

    private void spawnDrop(Entity dropEntity) {
        context.schedule(() -> {
            worldManager.bringIntoWorld(dropEntity);
            worldManager.spawn(dropEntity);
        });
    }
}
