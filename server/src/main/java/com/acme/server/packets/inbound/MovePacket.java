package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.position.MovementSystem;
import com.acme.server.position.WorldNode;
import com.acme.server.world.Position;

public class MovePacket extends InboundPacket {

    @Wire
    private MovementSystem movementSystem;

    private int x;
    private int y;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        movementSystem.moveTo(getClient().getNode(WorldNode.class), new Position(x, y));
    }
}
