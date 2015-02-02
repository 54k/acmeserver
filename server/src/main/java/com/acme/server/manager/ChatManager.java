package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.GameServerNetworkSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class ChatManager extends ManagerSystem {

    private GameServerNetworkSystem networkSystem;

    public void dispatchMessage(Entity sender, String message) {
        networkSystem.sendToSelfAndKnownList(sender, new ChatPacket(sender, message));
    }
}
