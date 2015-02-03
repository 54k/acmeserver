package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.controller.PickupController;
import com.acme.gameserver.manager.WorldManager;

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
