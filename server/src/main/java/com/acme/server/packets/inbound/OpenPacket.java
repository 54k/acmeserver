package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.inventory.PickupSystem;
import com.acme.server.managers.WorldManager;

@Wire
public class OpenPacket extends InboundPacket {

    private int chestId;

    private PickupSystem chestManager;

    private WorldManager worldManager;

    @Override
    public void read() {
        chestId = readInt();
    }

    @Override
    public void run() {
        chestManager.openChest(worldManager.getEntityById(chestId));
    }
}
