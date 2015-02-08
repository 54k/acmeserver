package com.acme.engine.network;

public interface SessionListener {

    void connected(Session session);

    void messageReceived(Session session, String message);

    void disconnected(Session session);
}
