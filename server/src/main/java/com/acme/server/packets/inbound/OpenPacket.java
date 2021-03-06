package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.inventory.PickupSystem;
import com.acme.server.model.system.passive.WorldSystem;

@Wire
public class OpenPacket extends InboundPacket {

    private int chestId;

    private PickupSystem chestManager;

    private WorldSystem worldSystem;

    @Override
    public void read() {
        chestId = readInt();
    }

    @Override
    public void run() {
        chestManager.openChest(worldSystem.getEntityById(chestId));
    }
}
