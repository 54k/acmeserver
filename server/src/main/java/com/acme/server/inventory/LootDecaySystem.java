package com.acme.server.inventory;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.timer.TimerSystem;
import com.acme.server.impacts.BlinkImpact;
import com.acme.server.impacts.BlinkImpactSystem;
import com.acme.server.managers.WorldManager;
import com.acme.server.packets.PacketSystem;
import com.acme.server.position.Transform;

@Wire
public class LootDecaySystem extends TimerSystem<LootDecay> {

    private ComponentMapper<Transform> positionCm;

    private Engine engine;
    private BlinkImpactSystem blinkImpactSystem;
    private WorldManager worldManager;
    private PacketSystem packetSystem;

    public LootDecaySystem() {
        super(LootDecay.class);
    }

    @Override
    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return positionCm.get(entity).isSpawned();
    }

    @Override
    protected void timerTicked(Entity entity, float deltaTime) {
        LootDecay lootDecayComponent = getTimer(entity);
        float time = lootDecayComponent.getTime();
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
