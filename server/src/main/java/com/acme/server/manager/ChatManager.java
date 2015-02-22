package com.acme.server.manager;

import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.aegis.systems.PassiveSystem;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.PacketSystem;

@Wired
public class ChatManager extends PassiveSystem {

    private PacketSystem packetSystem;

    public void dispatchMessage(Entity sender, String message) {
        packetSystem.sendToSelfAndRegion(sender, new ChatPacket(sender, message));
    }
}
