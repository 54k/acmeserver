package com.acme.server.packets.inbound;

import com.acme.ecs.core.Wire;
import com.acme.commons.network.InboundPacket;
import com.acme.server.combat.CombatSystem;
import com.acme.server.managers.WorldManager;

@Wire
public class HurtPacket extends InboundPacket {

    private CombatSystem combatSystem;

    private WorldManager worldManager;

    private long creatureId;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        combatSystem.attack(worldManager.getEntityById(creatureId), getClient());
    }
}
