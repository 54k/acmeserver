package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.combat.CombatSystem;
import com.acme.server.model.system.WorldSystem;

@Wire
public class HurtPacket extends InboundPacket {

    private CombatSystem combatSystem;

    private WorldSystem worldSystem;

    private long creatureId;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        combatSystem.attack(worldSystem.getEntityById(creatureId), getClient());
    }
}
