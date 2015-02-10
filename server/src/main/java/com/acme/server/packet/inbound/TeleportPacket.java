package com.acme.server.packet.inbound;

import com.acme.engine.ashley.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.controller.PositionController;
import com.acme.server.world.Position;

@Wired
public class TeleportPacket extends InboundPacket {

    private int x;
    private int y;

    private PositionController positionController;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        positionController.updatePosition(getClient(), new Position(x, y));
    }
}
