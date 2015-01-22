package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.manager.PickupManager;

public class LootPacket extends InboundPacket {

    @Wired
    private PickupManager pickupManager;

    private int itemId;

    @Override
    public void read() {
        itemId = readInt();
    }

    @Override
    public void run() {
        pickupManager.gatherPickup(getClient(), itemId);
    }
}
