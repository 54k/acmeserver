package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.manager.ChestManager;

public class OpenPacket extends InboundPacket {

    private int chestId;

    @Wired
    private ChestManager chestManager;

    @Override
    public void read() {
        chestId = readInt();
    }

    @Override
    public void run() {
        chestManager.openChest(getClient(), chestId);
    }
}
