package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.NetworkSystem;
import com.badlogic.ashley.core.Entity;

public class ChatManager extends ManagerSystem {

    @Wired
    private NetworkSystem networkSystem;

    public void dispatchMessage(Entity sender, String message) {
        networkSystem.sendToSelfAndKnownList(sender, new ChatPacket(sender, message));
    }
}
