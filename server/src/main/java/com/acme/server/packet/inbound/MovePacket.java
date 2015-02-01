package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.manager.PositionManager;
import com.acme.server.world.Position;

public class MovePacket extends InboundPacket {

    @Wired
    private PositionManager positionManager;

    private int x;
    private int y;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        positionManager.moveEntity(getClient(), new Position(x, y));
    }
}
