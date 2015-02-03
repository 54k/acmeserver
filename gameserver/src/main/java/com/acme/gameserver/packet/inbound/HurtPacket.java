package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.controller.CombatController;
import com.acme.gameserver.manager.WorldManager;

@Wired
public class HurtPacket extends InboundPacket {

    private CombatController combatController;

    private WorldManager worldManager;

    private long creatureId;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        combatController.attack(worldManager.findEntityById(creatureId), getClient());
    }
}
