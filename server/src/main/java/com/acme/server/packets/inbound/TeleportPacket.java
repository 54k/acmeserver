package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.model.node.PositionNode;
import com.acme.server.model.system.passive.PositionSystem;
import com.acme.server.world.Position;

@Wire
public class TeleportPacket extends InboundPacket {

    private int x;
    private int y;

    private PositionSystem positionSystem;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        positionSystem.teleportTo(getClient().getNode(PositionNode.class), new Position(x, y));
    }
}
