package com.acme.server.packet.inbound;

import com.acme.engine.ashley.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.manager.WorldManager;
import com.acme.server.pickup.PickupController;

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
        pickupController.gatherPickup(getClient(), worldManager.getEntityById(itemId));
    }
}
