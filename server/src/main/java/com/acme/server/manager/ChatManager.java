package com.acme.server.manager;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.packet.outbound.ChatPacket;
import com.acme.server.system.PacketSystem;

@Wire
public class ChatManager extends PassiveSystem {

    private PacketSystem packetSystem;

    public void dispatchMessage(Entity sender, String message) {
        packetSystem.sendToSelfAndRegion(sender, new ChatPacket(sender, message));
    }
}
