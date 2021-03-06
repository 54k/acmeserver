package com.acme.server.packets.outbound;

import com.acme.ecs.core.Entity;
import com.acme.commons.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

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
