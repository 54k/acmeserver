package com.acme.gameserver.packet.inbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.InboundPacket;
import com.acme.gameserver.manager.LoginManager;

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
