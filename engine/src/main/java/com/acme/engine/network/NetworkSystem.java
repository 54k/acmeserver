package com.acme.engine.network;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class NetworkSystem extends IteratingSystem implements SessionListener {

    private ComponentMapper<SessionComponent> scm = ComponentMapper.getFor(SessionComponent.class);

    private final Map<Session, Entity> entitiesBySession = new HashMap<>();

    public NetworkSystem() {
        //noinspection unchecked
        super(Family.all(SessionComponent.class).get(), Integer.MAX_VALUE);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        scm.get(entity).getSession().update();
    }

    @Override
    public void connected(Session session) {
        SessionComponent sessionComponent = new SessionComponent();
        sessionComponent.setSession(session);
        sessionComponent.setPacketReader(getPacketReader());
        Entity entity = createEntity(session);
        if (entity != null) {
            entity.add(sessionComponent);
            session.setListener(this);
            entitiesBySession.put(session, entity);
            connected(entity);
        }
    }

    protected abstract PacketReader getPacketReader();

    public abstract Entity createEntity(Session session);

    public void connected(Entity entity) {
    }

    @Override
    public void messageReceived(Session session, String message) {
        messageReceived(entitiesBySession.get(session), message);
    }

    public void messageReceived(Entity entity, String message) {
        Object[] data = deserializePacketData(message);
        SessionComponent sessionComponent = scm.get(entity);
        InboundPacket packet = sessionComponent.getPacketReader().readPacket(entity, data);
        if (packet != null) {
            packet.client = entity;
            packet.buffer = Arrays.copyOfRange(data, 1, data.length);
            runPacket(packet);
        }
    }

    protected void runPacket(InboundPacket packet) {
        packet.read();
        packet.run();
    }

    protected abstract Object[] deserializePacketData(String message);

    public void sendPacket(Entity receiver, OutboundPacket packet) {
        if (!scm.has(receiver)) {
            return;
        }
        packet.write();
        Object[] data = packet.getData();
        String serializedData = serializePacketData(data);
        scm.get(receiver).getSession().write(serializedData);
    }

    public void sendToAll(OutboundPacket packet) {
        packet.write();
        Object[] data = packet.getData();
        String serializedData = serializePacketData(data);
        entitiesBySession.values().stream()
                .map(scm::get).map(SessionComponent::getSession)
                .forEach(s -> s.write(serializedData));
    }

    protected abstract String serializePacketData(Object[] data);

    @Override
    public void disconnected(Session session) {
        Entity entity = entitiesBySession.remove(session);
        disconnected(entity);
    }

    public void disconnected(Entity entity) {
    }
}
