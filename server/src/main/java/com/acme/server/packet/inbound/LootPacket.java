package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.controller.PickupController;
import com.acme.server.manager.WorldManager;

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
