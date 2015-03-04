package com.acme.server.position;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.core.NodeMapper;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.IteratingSystem;
import com.acme.server.managers.WorldComponent;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.DespawnPacket;
import com.acme.server.packets.outbound.SpawnPacket;
import com.acme.server.utils.EntityContainer;
import com.acme.server.utils.PositionUtils;
import com.acme.server.world.Region;

public class KnownListSystem extends IteratingSystem {

    private static final Family KNOWN_LIST_OWNERS_FAMILY = Family.all(KnownList.class, Transform.class, WorldComponent.class).get();

    @Wire
    private NodeMapper<KnownListNode> knownListMapper;

    private PacketSystem packetSystem;

    public KnownListSystem() {
        super(KNOWN_LIST_OWNERS_FAMILY);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KnownListNode node = knownListMapper.get(entity);
        if (!node.getTransform().isSpawned()) {
            updateKnownList(node);
        }
    }

    private void updateKnownList(KnownListNode node) {
        forgetEntities(node);
        findEntities(node);
    }

    private void forgetEntities(KnownListNode node) {
        EntityContainer knownEntities = node.getKnownList().getKnownEntities();
        for (int i = knownEntities.size() - 1; i >= 0; i--) {
            Entity entity = knownEntities.get(i);
            if (forgetEntity(node, entity)) {
                event(KnownListListener.class).dispatch().entityRemoved(node, entity);
                packetSystem.sendPacket(node.getEntity(), new DespawnPacket(entity));
            }
        }
    }

    private boolean forgetEntity(KnownListNode node, Entity entity) {
        if (!node.getTransform().isSpawned() || (isKnownEntity(node, entity) && isOutOfRange(node, entity))) {
            removeFromKnownList(node, entity);
            return true;
        }
        return false;
    }

    private void findEntities(KnownListNode node) {

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
