package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.combat.HateListSystem;
import com.acme.server.managers.WorldManager;

@Wire
public class AggroPacket extends InboundPacket {

    private long creatureId;

    private HateListSystem hateListSystem;

    private WorldManager worldManager;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        hateListSystem.increaseHate(worldManager.getEntityById(creatureId), getClient(), 5);
    }
}
