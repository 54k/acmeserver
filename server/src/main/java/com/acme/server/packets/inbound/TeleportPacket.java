package com.acme.server.packets.inbound;

import com.acme.ecs.core.Wire;
import com.acme.commons.network.InboundPacket;
import com.acme.server.position.MoveSystem;
import com.acme.server.position.TransformNode;
import com.acme.server.world.Position;

@Wire
public class TeleportPacket extends InboundPacket {

    private int x;
    private int y;

    private MoveSystem moveSystem;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        moveSystem.teleportTo(getClient().getNode(TransformNode.class), new Position(x, y));
    }
}
