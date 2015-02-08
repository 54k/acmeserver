package com.acme.server.packet.inbound;

import com.acme.engine.ashley.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.server.manager.LoginManager;

public class LoginPacket extends InboundPacket {

    @Wired
    private LoginManager loginManager;

    private String name;
    private int weapon;
    private int armor;

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
