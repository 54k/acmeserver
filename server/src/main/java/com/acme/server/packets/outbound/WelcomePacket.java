package com.acme.server.packets.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

public class WelcomePacket extends OutboundPacket {

    private long id;
    private String name;
    private int x;
    private int y;
    private int hp;

    public WelcomePacket(long id, String name, int x, int y, int hp) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.hp = hp;
    }

    @Override
    public void write() {
        writeInt(OpCodes.WELCOME);
        writeLong(id);
        writeString(name);
        writeInt(x);
        writeInt(y);
        writeInt(hp);
    }
}
