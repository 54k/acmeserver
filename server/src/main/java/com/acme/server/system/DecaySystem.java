package com.acme.server.system;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.TimerSystem;
import com.acme.engine.effects.EffectSystem;
import com.acme.server.component.DecayComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.effects.EffectFactory;
import com.acme.server.manager.WorldManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

@Wired
public class DecaySystem extends TimerSystem<DecayComponent> {

    private ComponentMapper<PositionComponent> positionCm;

    private Engine engine;

    private EffectFactory effectFactory;
    private WorldManager worldManager;

    private EffectSystem effectSystem;
    private PacketSystem packetSystem;

    public DecaySystem() {
        super(DecayComponent.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return positionCm.get(entity).isSpawned();
    }

    @Override
    protected void timerTicked(Entity entity, float deltaTime) {
        DecayComponent decayComponent = getTimer(entity);
        float time = decayComponent.getTime();
        if (time <= 3000 && !effectSystem.hasEffect(EffectFactory.GLOBAL_BLINK_EFFECT, entity)) {
            Entity globalBlinkEffect = effectFactory.createGlobalBlinkEffect();
            effectSystem.applyEffect(globalBlinkEffect, entity);
        }
    }

    @Override
    protected void timerReady(Entity entity, float deltaTime) {
        worldManager.decay(entity);
        worldManager.removeFromWorld(entity);
        engine.removeEntity(entity);
    }
}
