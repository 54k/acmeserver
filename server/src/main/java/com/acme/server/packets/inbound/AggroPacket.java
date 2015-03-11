package com.acme.server.packets.inbound;

import com.acme.commons.network.InboundPacket;
import com.acme.ecs.core.Wire;
import com.acme.server.combat.HateListSystem;
import com.acme.server.model.system.WorldSystem;

@Wire
public class AggroPacket extends InboundPacket {

    private long creatureId;

    private HateListSystem hateListSystem;

    private WorldSystem worldSystem;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        hateListSystem.increaseHate(worldSystem.getEntityById(creatureId), getClient(), 5);
    }
}
