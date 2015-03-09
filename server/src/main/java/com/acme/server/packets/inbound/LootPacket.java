package com.acme.server.packets.inbound;

import com.acme.ecs.core.Wire;
import com.acme.commons.network.InboundPacket;
import com.acme.server.inventory.PickupSystem;
import com.acme.server.managers.WorldManager;

@Wire
public class LootPacket extends InboundPacket {

    private PickupSystem pickupSystem;

    private WorldManager worldManager;

    private int itemId;

    @Override
    public void read() {
        itemId = readInt();
    }

    @Override
    public void run() {
        pickupSystem.gatherPickup(getClient(), worldManager.getEntityById(itemId));
    }
}
