package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.model.node.TransformNode;
import com.acme.server.model.system.PositionSystem;
import com.acme.server.world.Position;

public class MovePacket extends InboundPacket {

    @Wire
    private PositionSystem positionSystem;

    private int x;
    private int y;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        positionSystem.moveTo(getClient().getNode(TransformNode.class), new Position(x, y));
    }
}
