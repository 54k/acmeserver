package com.acme.server.packet.inbound;

import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.InboundPacket;
import com.acme.server.manager.ChatManager;

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
