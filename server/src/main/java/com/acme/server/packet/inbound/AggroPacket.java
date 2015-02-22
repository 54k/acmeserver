package com.acme.server.packet.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.combat.HateListController;
import com.acme.server.manager.WorldManager;

@Wire
public class AggroPacket extends InboundPacket {

    private long creatureId;

    private HateListController hateListController;

    private WorldManager worldManager;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        hateListController.increaseHate(worldManager.getEntityById(creatureId), getClient(), 5);
    }
}
