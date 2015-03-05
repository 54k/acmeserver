package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.position.TransformSystem;
import com.acme.server.world.Position;

public class MovePacket extends InboundPacket {

    @Wire
    private TransformSystem transformSystem;

    private int x;
    private int y;

    @Override
    public void read() {
        x = readInt();
        y = readInt();
    }

    @Override
    public void run() {
        transformSystem.moveEntity(getClient(), new Position(x, y));
    }
}