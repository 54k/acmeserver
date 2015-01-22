package com.acme.commons.network;

import com.badlogic.ashley.core.Entity;

public abstract class InboundPacket implements Runnable {

    private int bufferPos = 0;

    Object[] buffer;
    Entity client;

    public abstract void read();

    protected Entity getClient() {
        return client;
    }

    protected int readInt() {
        return (int) buffer[bufferPos++];
    }

    protected long readLong() {
        return (long) buffer[bufferPos++];
    }

    protected String readString() {
        return (String) buffer[bufferPos++];
    }
}
