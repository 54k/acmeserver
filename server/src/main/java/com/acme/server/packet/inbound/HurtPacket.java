package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.controller.CombatController;
import com.acme.server.manager.WorldManager;

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
