package com.acme.server.packet.inbound;

import com.acme.engine.aegis.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.manager.WorldManager;
import com.acme.server.pickup.PickupController;

@Wired
public class OpenPacket extends InboundPacket {

    private int chestId;

    private PickupController chestManager;

    private WorldManager worldManager;

    @Override
    public void read() {
        chestId = readInt();
    }

    @Override
    public void run() {
        chestManager.openChest(worldManager.getEntityById(chestId));
    }
}
