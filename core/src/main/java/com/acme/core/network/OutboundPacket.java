package com.acme.core.network;

import java.util.ArrayList;
import java.util.List;

public abstract class OutboundPacket {

    private final List<Object> data = new ArrayList<>();

    protected void writeInt(int i) {
        data.add(i);
    }

    protected void writeLong(long l) {
        data.add(l);
    }

    protected void writeString(String s) {
        data.add(s);
    }

    public abstract void write();

    public Object[] getData() {
        return data.toArray();
    }
}
