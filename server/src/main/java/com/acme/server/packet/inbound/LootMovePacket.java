package com.acme.server.packet.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.controller.PositionController;
import com.acme.server.world.Position;

public class LootMovePacket extends InboundPacket {

    @Wire
    private PositionController positionController;

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
        positionController.moveEntity(getClient(), new Position(x, y));
    }
}
