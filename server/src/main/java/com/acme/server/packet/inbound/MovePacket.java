package com.acme.server.packet.inbound;

import com.acme.engine.aegis.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.controller.PositionController;
import com.acme.server.world.Position;

public class MovePacket extends InboundPacket {

    @Wired
    private PositionController positionController;

    private int x;
    private int y;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        positionController.moveEntity(getClient(), new Position(x, y));
    }
}
