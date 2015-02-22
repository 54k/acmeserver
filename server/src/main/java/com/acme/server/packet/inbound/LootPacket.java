package com.acme.server.packet.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.manager.WorldManager;
import com.acme.server.pickup.PickupController;

@Wire
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
