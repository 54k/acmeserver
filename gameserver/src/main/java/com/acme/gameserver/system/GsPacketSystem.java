package com.acme.gameserver.system;

import com.acme.core.ashley.EntityEngine;
import com.acme.core.ashley.Wired;
import com.acme.core.network.*;
import com.acme.gameserver.component.KnownListComponent;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.manager.EntityManager;
import com.acme.gameserver.packet.OpCodes;
import com.acme.gameserver.packet.PacketReader;
import com.acme.gameserver.packet.inbound.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Wired
public class GsPacketSystem extends PacketSystem {

    private ComponentMapper<SessionComponent> scm;
    private ComponentMapper<KnownListComponent> kcm;
    private ComponentMapper<PositionComponent> pcm;

    private EntityEngine engine;
    private EntityManager entityManager;

    private ObjectMapper objectMapper;

    private final PacketReader packetReader;

    public GsPacketSystem() {
        packetReader = new PacketReader();
        packetReader.registerPacketPrototype(OpCodes.HELLO, LoginPacket.class);
        packetReader.registerPacketPrototype(OpCodes.MOVE, MovePacket.class);
        packetReader.registerPacketPrototype(OpCodes.CHAT, ChatPacket.class);
        packetReader.registerPacketPrototype(OpCodes.LOOTMOVE, LootMovePacket.class);
        packetReader.registerPacketPrototype(OpCodes.LOOT, LootPacket.class);
        packetReader.registerPacketPrototype(OpCodes.OPEN, OpenPacket.class);
        packetReader.registerPacketPrototype(OpCodes.ATTACK, AttackPacket.class);
        packetReader.registerPacketPrototype(OpCodes.HIT, HitPacket.class);
        packetReader.registerPacketPrototype(OpCodes.HURT, HurtPacket.class);
        packetReader.registerPacketPrototype(OpCodes.AGGRO, AggroPacket.class);
    }

    @Override
    public Entity createEntity(Session session) {
        return entityManager.createPlayer();
    }

    @Override
    public void connected(Entity entity) {
        scm.get(entity).getSession().write("go");
    }

    @Override
    protected void runPacket(InboundPacket packet) {
        engine.wireObject(packet);
        super.runPacket(packet);
    }

    protected Object[] deserializePacketData(String message) {
        try {
            return objectMapper.readValue(message, Object[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String serializePacketData(Object[] data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected com.acme.core.network.PacketReader getPacketReader() {
        return packetReader;
    }

    @Override
    public void sendPacket(Entity receiver, OutboundPacket packet) {
        engine.wireObject(packet);
        super.sendPacket(receiver, packet);
    }

    @Override
    public void sendToAll(OutboundPacket packet) {
        engine.wireObject(packet);
        super.sendToAll(packet);
    }

    public void sendToSelfAndKnownList(Entity sender, OutboundPacket packet) {
        sendPacket(sender, packet);
        sendToKnownList(sender, packet);
    }

    public void sendToKnownList(Entity sender, OutboundPacket packet) {
        kcm.get(sender).getKnownPlayers().forEach(e -> sendPacket(e, packet));
    }

    public void sendToSelfAndRegion(Entity sender, OutboundPacket packet) {
        sendPacket(sender, packet);
        pcm.get(sender).getRegion()
                .getSurroundingRegions()
                .stream().flatMap(r -> r.getPlayers().values().stream())
                .forEach(e -> sendPacket(e, packet));
    }

    @Override
    public void disconnected(Entity entity) {
        engine.removeEntity(entity);
    }
}
