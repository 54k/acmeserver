package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.manager.PositionManager;
import com.acme.server.world.Position;

public class LootMovePacket extends InboundPacket {

    @Wired
    private PositionManager positionManager;

    private int x;
    private int y;
    private int itemId;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
        itemId = readInt();
    }

    @Override
    public void run() {
        positionManager.moveEntity(getClient(), new Position(x, y));
    }
}
