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

    private final Map<Entity, BlinkEffectHandler> affected = new HashMap<>();

    public void addBlinkEffect(Entity entity, float duration) {
        BlinkEffectHandler blinkEffectHandler = new BlinkEffectHandler(duration);
        affected.putIfAbsent(entity, blinkEffectHandler);
        getEffectList(entity).apply(blinkEffectHandler);
    }

    public void removeBlinkEffect(Entity entity) {
        BlinkEffectHandler blinkEffectHandler = affected.get(entity);
        if (blinkEffectHandler != null) {
            getEffectList(entity).remove(blinkEffectHandler);
        }
    }

    public boolean isBlinking(Entity entity) {
        return affected.containsKey(entity);
    }

    @Override
    public void remove(Entity entity) {
        affected.remove(entity);
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

    private final class BlinkEffectHandler extends TimedEffect<Entity> {

        BlinkEffectHandler(float duration) {
            super(duration);
        }

        @Override
        public boolean stack(Entity entity, Effect<Entity> effect) {
            BlinkEffectHandler blinkEffectHandler = (BlinkEffectHandler) effect;
            float duration = blinkEffectHandler.getDuration();
            if (duration > getDuration()) {
                setTicks(blinkEffectHandler.getTicks());
                setStepTime(blinkEffectHandler.getStepTime());
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

        @Override
        public void remove(Entity entity) {
            BlinkController.this.remove(entity);
        }
    }
}
