package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.manager.ChatManager;

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
