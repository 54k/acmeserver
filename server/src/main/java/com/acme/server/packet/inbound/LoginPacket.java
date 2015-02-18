package com.acme.server.packet.inbound;

import com.acme.engine.processors.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.manager.LoginManager;

@Wired
public class LoginPacket extends InboundPacket {

    private String name;
    private int weapon;
    private int armor;

    private LoginManager loginManager;

    @Override
    public void read() {
        name = readString();
        armor = readInt();
        weapon = readInt();
    }

    @Override
    public void run() {
        loginManager.login(getClient(), name, weapon, armor);
    }
}
