package com.acme.server.packet.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.manager.WorldManager;
import com.acme.server.pickups.PickupController;

@Wire
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
        chestManager.openChest(worldManager.getEntityById(chestId));
    }
}
