package com.acme.server.packet.inbound;

import com.acme.engine.aegis.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.combat.CombatController;
import com.acme.server.manager.WorldManager;

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
        combatController.attack(getClient(), worldManager.getEntityById(targetId));
    }
}
