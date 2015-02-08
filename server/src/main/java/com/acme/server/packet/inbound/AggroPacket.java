package com.acme.server.packet.inbound;

import com.acme.engine.ashley.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.controller.HateController;
import com.acme.server.manager.WorldManager;

@Wired
public class AggroPacket extends InboundPacket {

    private long creatureId;

    private HateController hateController;

    private WorldManager worldManager;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        hateController.increaseHate(worldManager.getEntityById(creatureId), getClient(), 5);
    }
}
