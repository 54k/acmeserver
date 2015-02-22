package com.acme.server.system;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Engine;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.timer.TimerSystem;
import com.acme.server.component.Decay;
import com.acme.server.component.PositionComponent;
import com.acme.server.impact.BlinkImpact;
import com.acme.server.impact.BlinkImpactSystem;
import com.acme.server.manager.WorldManager;

@Wired
public class DecaySystem extends TimerSystem<Decay> {

    private ComponentMapper<PositionComponent> positionCm;

    private Engine engine;
    private BlinkImpactSystem blinkImpactSystem;
    private WorldManager worldManager;
    private PacketSystem packetSystem;

    public DecaySystem() {
        super(Decay.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return positionCm.get(entity).isSpawned();
    }

    @Override
    protected void timerTicked(Entity entity, float deltaTime) {
        Decay decayComponent = getTimer(entity);
        float time = decayComponent.getTime();
        if (time <= 3000 && !blinkImpactSystem.hasImpact(entity)) {
            entity.add(new BlinkImpact());
        }
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        worldManager.decay(entity);
        worldManager.removeFromWorld(entity);
        engine.removeEntity(entity);
    }
}
