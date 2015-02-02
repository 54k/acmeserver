package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
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
        hateController.increaseHate(worldManager.findEntityById(creatureId), getClient(), 5);
    }
}
