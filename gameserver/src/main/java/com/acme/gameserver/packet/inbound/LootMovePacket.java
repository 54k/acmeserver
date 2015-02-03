package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.controller.PositionController;
import com.acme.gameserver.world.Position;

public class LootMovePacket extends InboundPacket {

    @Wired
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
