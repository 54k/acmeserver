package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.position.MovementSystem;
import com.acme.server.position.WorldNode;
import com.acme.server.world.Position;

@Wire
public class TeleportPacket extends InboundPacket {

    private int x;
    private int y;

    private MovementSystem movementSystem;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        movementSystem.setPosition(getClient().getNode(WorldNode.class), new Position(x, y));
    }
}
