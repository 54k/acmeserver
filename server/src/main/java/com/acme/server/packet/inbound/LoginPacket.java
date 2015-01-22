package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
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
        weapon = readInt();
        armor = readInt();
    }

    @Override
    public void run() {
        loginManager.login(getClient(), name, weapon, armor);
    }
}
