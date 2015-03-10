package com.acme.server.packets.inbound;

import com.acme.ecs.core.Wire;
import com.acme.commons.network.InboundPacket;
import com.acme.server.position.MoveSystem;
import com.acme.server.position.TransformNode;
import com.acme.server.world.Position;

public class LootMovePacket extends InboundPacket {

    @Wire
    private MoveSystem moveSystem;

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
        moveSystem.moveTo(getClient().getNode(TransformNode.class), new Position(x, y));
    }
}
