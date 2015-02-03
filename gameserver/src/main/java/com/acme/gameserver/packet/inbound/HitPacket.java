package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.controller.CombatController;
import com.acme.gameserver.manager.WorldManager;

@Wired
public class HitPacket extends InboundPacket {

    private CombatController combatController;

    private WorldManager worldManager;

    private long targetId;

    @Override
    public void read() {
        targetId = readInt();
    }

    @Override
    public void run() {
        combatController.attack(getClient(), worldManager.findEntityById(targetId));
    }
}
