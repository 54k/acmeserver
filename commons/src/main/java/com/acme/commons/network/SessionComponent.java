package com.acme.commons.network;

import com.badlogic.ashley.core.Component;

public class SessionComponent extends Component {

    private Session session;
    private PacketReader packetReader;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public PacketReader getPacketReader() {
        return packetReader;
    }

    public void setPacketReader(PacketReader packetReader) {
        this.packetReader = packetReader;
    }
}
