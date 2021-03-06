package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.model.node.PositionNode;
import com.acme.server.model.system.active.PositionSystem;
import com.acme.server.world.Position;

public class LootMovePacket extends InboundPacket {

    @Wire
    private PositionSystem positionSystem;

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
        positionSystem.moveTo(getClient().getNode(PositionNode.class), new Position(x, y));
    }
}
