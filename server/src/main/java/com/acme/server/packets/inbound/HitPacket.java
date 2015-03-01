package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.combat.CombatSystem;
import com.acme.server.managers.WorldManager;

@Wire
public class HitPacket extends InboundPacket {

    private CombatSystem combatSystem;

    private WorldManager worldManager;

    private long targetId;

    @Override
    public void read() {
        targetId = readInt();
    }

    @Override
    public void run() {
        combatSystem.attack(getClient(), worldManager.getEntityById(targetId));
    }
}
