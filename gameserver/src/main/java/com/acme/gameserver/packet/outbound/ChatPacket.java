package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.packet.OpCodes;
import com.badlogic.ashley.core.Entity;

public class ChatPacket extends OutboundPacket {

    private Entity sender;
    private String message;

    public ChatPacket(Entity sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void write() {
        writeInt(OpCodes.CHAT);
        writeLong(sender.getId());
        writeString(message);
    }
}
