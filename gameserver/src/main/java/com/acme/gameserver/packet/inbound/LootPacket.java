package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.controller.PickupController;
import com.acme.gameserver.manager.WorldManager;

@Wired
public class LootPacket extends InboundPacket {

    private PickupController pickupController;

    private WorldManager worldManager;

    private int itemId;

    @Override
    public void read() {
        itemId = readInt();
    }

    @Override
    public void run() {
        pickupController.gatherPickup(getClient(), worldManager.findEntityById(itemId));
    }
}
