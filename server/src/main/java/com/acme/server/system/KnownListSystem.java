package com.acme.server.system;

import com.acme.engine.processors.Wired;
import com.acme.server.component.KnownListComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.DespawnPacket;
import com.acme.server.packet.outbound.SpawnPacket;
import com.acme.server.util.EntityContainer;
import com.acme.server.util.PositionUtils;
import com.acme.server.world.Region;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

@Wired
public class KnownListSystem extends IteratingSystem {

    private static final Family KNOWN_LIST_OWNERS_FAMILY = Family.all(KnownListComponent.class, PositionComponent.class, WorldComponent.class).get();

    private ComponentMapper<KnownListComponent> kcm;
    private ComponentMapper<PositionComponent> pcm;

    private PacketSystem packetSystem;

    public KnownListSystem() {
        super(KNOWN_LIST_OWNERS_FAMILY);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (pcm.get(entity).isSpawned()) {
            updateKnownList(entity);
        }
    }

    public void clearKnownList(Entity owner) {
        kcm.get(owner).getKnownEntities().clear();
    }

    public void updateKnownList(Entity owner) {
        forgetEntities(owner);
        findEntities(owner);
    }

    public void forgetEntities(Entity owner) {
        EntityContainer knownEntities = kcm.get(owner).getKnownEntities();
        for (int i = knownEntities.size() - 1; i >= 0; i--) {
            Entity entity = knownEntities.get(i);
            if (removeEntity(owner, entity)) {
                packetSystem.sendPacket(owner, new DespawnPacket(entity));
            }
        }
    }

    private boolean removeEntity(Entity owner, Entity entity) {
        if (!pcm.get(entity).isSpawned() || (isKnownEntity(owner, entity) && isOutOfRange(owner, entity))) {
            removeFromKnownList(owner, entity);
            return true;
        }
        return false;
    }

    private boolean isOutOfRange(Entity owner, Entity entity) {
        int distanceToForgetEntity = kcm.get(owner).getDistanceToForgetEntity();
        return PositionUtils.isOutOfRange(entity, owner, distanceToForgetEntity);
    }

    private void removeFromKnownList(Entity owner, Entity entity) {
        kcm.get(owner).getKnownEntities().remove(entity);
    }

    public void findEntities(Entity owner) {
        Region region = pcm.get(owner).getRegion();
        region.getSurroundingRegions().stream()
                .flatMap(r -> r.getEntities().stream())
                .filter(e -> addObject(owner, e))
                .forEach(o -> packetSystem.sendPacket(owner, new SpawnPacket(o)));
    }

    private boolean addObject(Entity owner, Entity entity) {
        if (pcm.get(entity).isSpawned() && !isKnownEntity(owner, entity) && isInRange(owner, entity)) {
            addToKnownList(owner, entity);
            return true;
        }
        return false;
    }

    private boolean isInRange(Entity owner, Entity entity) {
        int distanceToFindEntity = kcm.get(owner).getDistanceToFindEntity();
        return PositionUtils.isInRange(entity, owner, distanceToFindEntity);
    }

    private void addToKnownList(Entity owner, Entity entity) {
        kcm.get(owner).getKnownEntities().add(entity);
    }

    private boolean isKnownEntity(Entity owner, Entity entity) {
        return owner == entity || kcm.get(owner).getKnownEntities().contains(entity);
    }
}
