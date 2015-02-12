package com.acme.server.system;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.TimerSystem;
import com.acme.server.component.DecayComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.controller.BlinkController;
import com.acme.server.manager.WorldManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

@Wired
public class DecaySystem extends TimerSystem<DecayComponent> {

    private ComponentMapper<PositionComponent> pcm;

    private Engine engine;
    private BlinkController blinkController;
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
        float time = decayComponent.getTime();
        if (time <= 3000 && !blinkController.isBlinking(entity)) {
            blinkController.addBlinkEffect(entity, time);
        }
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        worldManager.decay(entity);
        worldManager.removeFromWorld(entity);
        engine.removeEntity(entity);
    }
}
