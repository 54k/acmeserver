package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.position.TransformSystem;
import com.acme.server.world.Position;

@Wire
public class TeleportPacket extends InboundPacket {

    private int x;
    private int y;

    private TransformSystem transformSystem;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        transformSystem.updatePosition(getClient(), new Position(x, y));
    }
}
