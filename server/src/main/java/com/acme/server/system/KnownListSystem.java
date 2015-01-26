package com.acme.server.system;

import com.acme.commons.ashley.Wired;
import com.acme.server.component.KnownListComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.DespawnPacket;
import com.acme.server.packet.outbound.SpawnPacket;
import com.acme.server.util.EntityUtils;
import com.acme.server.util.PositionUtils;
import com.acme.server.world.Region;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.List;

@Wired
public class KnownListSystem extends IteratingSystem {

    private ComponentMapper<KnownListComponent> kcm;
    private ComponentMapper<PositionComponent> pcm;

    private NetworkSystem networkSystem;

    private Entity actor;
    private List<Entity> knownEntities;
    private List<Entity> knownPlayers;
    private int distanceToFindEntity;
    private int distanceToForgetEntity;

    public KnownListSystem() {
        //noinspection unchecked
        super(Family.all(KnownListComponent.class, PositionComponent.class, WorldComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (pcm.get(entity).isSpawned()) {
            processKnownList(entity);
        }
    }

    private void processKnownList(Entity entity) {
        actor = entity;
        KnownListComponent knownListComponent = kcm.get(actor);
        knownEntities = knownListComponent.getKnownEntities();
        knownPlayers = knownListComponent.getKnownPlayers();
        distanceToFindEntity = knownListComponent.getDistanceToFindEntity();
        distanceToForgetEntity = knownListComponent.getDistanceToForgetEntity();

        forgetEntities();
        findEntities();
    }

    private void forgetEntities() {
        for (int i = knownEntities.size() - 1; i >= 0; i--) {
            Entity entity = knownEntities.get(i);
            if (removeEntity(entity)) {
                networkSystem.sendPacket(actor, new DespawnPacket(entity));
            }
        }
    }

    private boolean removeEntity(Entity entity) {
        if (isKnownEntity(entity) && isOutOfRange(entity)) {
            removeFromKnownList(entity);
            return true;
        }
        return false;
    }

    private boolean isOutOfRange(Entity entity) {
        return PositionUtils.isOutOfRange(entity, actor, distanceToForgetEntity) || !pcm.get(entity).isSpawned();
    }

    private void removeFromKnownList(Entity entity) {
        knownEntities.remove(entity);
        if (EntityUtils.isPlayer(entity)) {
            knownPlayers.remove(entity);
        }
    }

    private void findEntities() {
        Region region = pcm.get(actor).getRegion();
        region.getSurroundingRegions().stream()
                .flatMap(r -> r.getEntities().values().stream())
                .filter(this::addObject)
                .forEach(o -> networkSystem.sendPacket(actor, new SpawnPacket(o)));
    }

    private boolean addObject(Entity entity) {
        if (!isKnownEntity(entity) && isInRange(entity)) {
            addToKnownList(entity);
            return true;
        }
        return false;
    }

    private boolean isInRange(Entity entity) {
        return PositionUtils.isInRange(entity, actor, distanceToFindEntity) && pcm.get(entity).isSpawned();
    }

    private void addToKnownList(Entity entity) {
        knownEntities.add(entity);
        if (EntityUtils.isPlayer(entity)) {
            knownPlayers.add(entity);
        }
    }

    private boolean isKnownEntity(Entity entity) {
        return actor == entity || kcm.get(actor).getKnownEntities().contains(entity);
    }
}
