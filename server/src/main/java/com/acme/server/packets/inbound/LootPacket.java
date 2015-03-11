package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.inventory.PickupSystem;
import com.acme.server.model.system.WorldSystem;

@Wire
public class LootPacket extends InboundPacket {

    private PickupSystem pickupSystem;

    private WorldSystem worldSystem;

    private int itemId;

    @Override
    public void read() {
        itemId = readInt();
    }

    @Override
    public void run() {
        pickupSystem.gatherPickup(getClient(), worldSystem.getEntityById(itemId));
    }
}
