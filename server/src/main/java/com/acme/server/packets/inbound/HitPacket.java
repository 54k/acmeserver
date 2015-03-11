package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.combat.CombatSystem;
import com.acme.server.model.system.WorldSystem;

@Wire
public class HitPacket extends InboundPacket {

    private CombatSystem combatSystem;

    private WorldSystem worldSystem;

    private long targetId;

    @Override
    public void read() {
        targetId = readInt();
    }

    @Override
    public void run() {
        combatSystem.attack(getClient(), worldSystem.getEntityById(targetId));
    }
}
