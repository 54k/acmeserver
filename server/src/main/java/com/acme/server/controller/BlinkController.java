package com.acme.server.controller;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.EffectController;
import com.acme.engine.effect.Effect;
import com.acme.engine.effect.TimedEffect;
import com.acme.server.component.PositionComponent;
import com.acme.server.event.CombatControllerEvent;
import com.acme.server.packet.outbound.BlinkPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

@Wired
public class BlinkController extends EffectController implements CombatControllerEvent {

    private ComponentMapper<PositionComponent> pcm;

    private PacketSystem packetSystem;

    private final Map<Entity, BlinkEffect> blinkers = new HashMap<>();

    public void addBlinkEffect(Entity entity, float duration) {
        BlinkEffect e2 = new BlinkEffect(duration);
        BlinkEffect e1 = blinkers.putIfAbsent(entity, e2);
        if (e1 == null) {
            getEffectList(entity).apply(this);
        } else {
            e1.stack(entity, e2);
        }
    }

    public void removeBlinkEffect(Entity entity) {
        getEffectList(entity).remove(this);
    }

    public boolean isBlinking(Entity entity) {
        return blinkers.containsKey(entity);
    }

    @Override
    public void apply(Entity entity) {
        blinkers.get(entity).apply(entity);
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        blinkers.get(entity).update(entity, deltaTime);
    }

    @Override
    public void remove(Entity entity) {
        blinkers.get(entity).remove(entity);
        blinkers.remove(entity);
    }

    private void sendBlinkPacket(Entity entity) {
        packetSystem.sendToSelfAndRegion(entity, new BlinkPacket(entity.getId()));
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (isBlinking(entity)) {
            removeBlinkEffect(entity);
        }
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        if (isBlinking(victim)) {
            removeBlinkEffect(victim);
        }
    }

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
    }

    private final class BlinkEffect extends TimedEffect<Entity> {

        BlinkEffect(float duration) {
            super(duration);
        }

        @Override
        public boolean stack(Entity entity, Effect<Entity> effect) {
            BlinkEffect blinkEffect = (BlinkEffect) effect;
            float duration = blinkEffect.getDuration();
            if (duration > getDuration()) {
                setTicks(blinkEffect.getTicks());
                setStepTime(blinkEffect.getStepTime());
                resetCurrentTime();
            }
            return true;
        }

        @Override
        public void apply(Entity entity) {
            sendBlinkPacket(entity);
        }

        @Override
        public void ready(Entity entity) {
            removeBlinkEffect(entity);
        }
    }
}
