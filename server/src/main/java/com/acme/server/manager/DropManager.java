package com.acme.server.manager;

import com.acme.commons.application.Context;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.*;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class DropManager extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<DropComponent> dcm;
    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<SpawnComponent> scm;

    private Context context;
    private EntityManager entityManager;
    private WorldManager worldManager;

    public void dropItems(Entity entity) {
        WorldComponent worldComponent = wcm.get(entity);
        DropComponent dropComponent = dcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        SpawnComponent spawnComponent = scm.get(entity);
        Area dropArea = new Area(positionComponent.getX() - 1, positionComponent.getY() - 1, 2, 2);

        for (DropComponent.Drop drop : dropComponent.getDrops()) {
            if (!dropSucceed(drop)) {
                continue;
            }

            Entity dropEntity = entityManager.createEntity(drop.getType());
            wcm.get(dropEntity).setInstance(worldComponent.getInstance());
            Position dropPosition = PositionUtils.getRandomPositionInside(dropArea);
            pcm.get(dropEntity).setPosition(dropPosition);
            DespawnComponent despawnComponent = new DespawnComponent();
            despawnComponent.setCooldown(spawnComponent.getCooldown());
            dropEntity.add(despawnComponent);
            context.schedule(() -> {
                worldManager.bringIntoWorld(dropEntity);
                worldManager.spawn(dropEntity);
            });
        }
    }

    private boolean dropSucceed(DropComponent.Drop drop) {
        return drop.getChance() >= Rnd.between(0, 100);
    }
}
