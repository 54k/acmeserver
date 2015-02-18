package com.acme.server.packet.inbound;

import com.acme.engine.processors.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.manager.ChatManager;

public class ChatPacket extends InboundPacket {

    @Wired
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
