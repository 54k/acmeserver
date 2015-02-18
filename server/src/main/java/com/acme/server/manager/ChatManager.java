package com.acme.server.manager;

import com.acme.engine.systems.PassiveSystem;
import com.acme.engine.processors.Wired;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class ChatManager extends PassiveSystem {

    private PacketSystem packetSystem;

    public void dispatchMessage(Entity sender, String message) {
        packetSystem.sendToSelfAndRegion(sender, new ChatPacket(sender, message));
    }
}
