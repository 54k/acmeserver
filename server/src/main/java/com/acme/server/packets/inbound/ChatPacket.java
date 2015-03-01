package com.acme.server.packets.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.managers.ChatManager;

public class ChatPacket extends InboundPacket {

    @Wire
    private ChatManager chatManager;

    private String message;

    @Override
    public void read() {
        message = readString();
    }

    @Override
    public void run() {
        chatManager.dispatchMessage(getClient(), message);
    }
}
