package com.acme.gameserver.manager;

import com.acme.core.ashley.ManagerSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.packet.outbound.ChatPacket;
import com.acme.gameserver.system.GsPacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class ChatManager extends ManagerSystem {

    private GsPacketSystem gsPacketSystem;

    public void dispatchMessage(Entity sender, String message) {
        gsPacketSystem.sendToSelfAndRegion(sender, new ChatPacket(sender, message));
    }
}
