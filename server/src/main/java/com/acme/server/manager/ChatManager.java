package com.acme.server.manager;

import com.acme.engine.systems.ManagerSystem;
import com.acme.engine.aegis.Wired;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class ChatManager extends ManagerSystem {

    private PacketSystem packetSystem;

    public void dispatchMessage(Entity sender, String message) {
        packetSystem.sendToSelfAndRegion(sender, new ChatPacket(sender, message));
    }
}
