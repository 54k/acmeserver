package com.acme.server.packets.inbound;

import com.acme.ecs.core.Wire;
import com.acme.commons.network.InboundPacket;
import com.acme.server.combat.CombatSystem;
import com.acme.server.managers.WorldManager;

@Wire
public class AttackPacket extends InboundPacket {

    private CombatSystem combatSystem;

    private WorldManager worldManager;

    private long targetId;

    @Override
    public void read() {
        targetId = readInt();
    }

    @Override
    public void run() {
        combatSystem.engage(getClient(), worldManager.getEntityById(targetId));
    }
}
