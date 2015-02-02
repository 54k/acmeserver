package com.acme.server.system;

import com.acme.commons.ashley.Wired;
import com.acme.server.component.DespawnComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.manager.WorldManager;
import com.acme.server.packet.outbound.BlinkPacket;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

@Wired
public class DespawnSystem extends IteratingSystem {

    private ComponentMapper<DespawnComponent> dcm;
    private ComponentMapper<PositionComponent> pcm;

    private Engine engine;
    private WorldManager worldManager;
    private PacketSystem packetSystem;

    public DespawnSystem() {
        super(Family.all(DespawnComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DespawnComponent despawnComponent = dcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        float despawnCooldown = 0;
        if (positionComponent.isSpawned()) {
            despawnCooldown = despawnComponent.getCooldown() - deltaTime;
        }
        despawnComponent.setCooldown(despawnCooldown);

        if (despawnCooldown <= 4000 && !despawnComponent.isBlinking()) {
            packetSystem.sendToSelfAndRegion(entity, new BlinkPacket(entity.getId()));
            despawnComponent.setBlinking(true);
        }

        if (despawnCooldown <= 0) {
            worldManager.decay(entity);
            worldManager.removeFromWorld(entity);
            engine.removeEntity(entity);
        }
    }
}
