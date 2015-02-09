package com.acme.server.system;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.TimerSystem;
import com.acme.server.component.DecayComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.manager.WorldManager;
import com.acme.server.packet.outbound.BlinkPacket;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

@Wired
public class DecaySystem extends TimerSystem<DecayComponent> {

    private ComponentMapper<PositionComponent> pcm;

    private Engine engine;
    private WorldManager worldManager;
    private PacketSystem packetSystem;

    public DecaySystem() {
        super(DecayComponent.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return pcm.get(entity).isSpawned();
    }

    @Override
    protected void timerTicked(Entity entity, float deltaTime) {
        DecayComponent decayComponent = getTimer(entity);
        if (decayComponent.getTime() <= 3000.0 && !decayComponent.isBlinking()) {
            packetSystem.sendToSelfAndRegion(entity, new BlinkPacket(entity.getId()));
            decayComponent.setBlinking(true);
        }
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        worldManager.decay(entity);
        worldManager.removeFromWorld(entity);
        engine.removeEntity(entity);
    }
}
