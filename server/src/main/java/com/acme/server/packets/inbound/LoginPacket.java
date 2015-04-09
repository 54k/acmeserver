package com.acme.server.packets.inbound;

import com.acme.ecs.core.Wire;
import com.acme.commons.network.InboundPacket;
import com.acme.server.managers.LoginManager;

@Wire
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
