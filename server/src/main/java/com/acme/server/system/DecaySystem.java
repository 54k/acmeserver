package com.acme.server.system;

import com.acme.commons.ashley.Wired;
import com.acme.server.component.DecayComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.manager.WorldManager;
import com.acme.server.packet.outbound.BlinkPacket;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

@Wired
public class DecaySystem extends IteratingSystem {

    private ComponentMapper<DecayComponent> dcm;
    private ComponentMapper<PositionComponent> pcm;

    private Engine engine;
    private WorldManager worldManager;
    private PacketSystem packetSystem;

    public DecaySystem() {
        super(Family.all(DecayComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DecayComponent decayComponent = dcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        float despawnCooldown = 0;
        if (positionComponent.isSpawned()) {
            despawnCooldown = decayComponent.getCooldown() - deltaTime;
        }
        decayComponent.setCooldown(despawnCooldown);

        if (despawnCooldown <= 4000 && !decayComponent.isBlinking()) {
            packetSystem.sendToSelfAndRegion(entity, new BlinkPacket(entity.getId()));
            decayComponent.setBlinking(true);
        }

        if (despawnCooldown <= 0) {
            worldManager.decay(entity);
            worldManager.removeFromWorld(entity);
            engine.removeEntity(entity);
        }
    }
}
