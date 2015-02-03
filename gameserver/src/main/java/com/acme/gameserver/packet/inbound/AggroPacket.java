package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.controller.HateController;
import com.acme.gameserver.manager.WorldManager;

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
