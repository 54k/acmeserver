package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.controller.PickupController;
import com.acme.server.manager.WorldManager;

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
        chestManager.openChest(worldManager.findEntityById(chestId));
    }
}
