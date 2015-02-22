package com.acme.server.packet.outbound;

import com.acme.engine.aegis.core.Entity;
import com.acme.engine.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

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
