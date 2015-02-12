package com.acme.server.system;

import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.ashley.Wired;
import com.acme.engine.network.InboundPacket;
import com.acme.engine.network.NetworkSystem;
import com.acme.engine.network.OutboundPacket;
import com.acme.engine.network.Session;
import com.acme.engine.network.SessionComponent;
import com.acme.server.component.KnownListComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.manager.EntityManager;
import com.acme.server.packet.OpCodes;
import com.acme.server.packet.PacketReader;
import com.acme.server.packet.inbound.AggroPacket;
import com.acme.server.packet.inbound.AttackPacket;
import com.acme.server.packet.inbound.ChatPacket;
import com.acme.server.packet.inbound.CheckpointPacket;
import com.acme.server.packet.inbound.HitPacket;
import com.acme.server.packet.inbound.HurtPacket;
import com.acme.server.packet.inbound.LoginPacket;
import com.acme.server.packet.inbound.LootMovePacket;
import com.acme.server.packet.inbound.LootPacket;
import com.acme.server.packet.inbound.MovePacket;
import com.acme.server.packet.inbound.OpenPacket;
import com.acme.server.packet.inbound.TeleportPacket;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Wired
public class PacketSystem extends NetworkSystem {

    private ComponentMapper<SessionComponent> scm;
    private ComponentMapper<KnownListComponent> kcm;
    private ComponentMapper<PositionComponent> pcm;

    private EntityEngine engine;
    private EntityManager entityManager;

    private ObjectMapper objectMapper;

    private final PacketReader packetReader;

    public PacketSystem() {
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
        packetReader.registerPacketPrototype(OpCodes.TELEPORT, TeleportPacket.class);
        packetReader.registerPacketPrototype(OpCodes.CHECK, CheckpointPacket.class);
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
    protected com.acme.engine.network.PacketReader getPacketReader() {
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
        kcm.get(sender).getKnownEntities().getPlayers().forEach(e -> sendPacket(e, packet));
    }

    public void sendToSelfAndRegion(Entity sender, OutboundPacket packet) {
        sendPacket(sender, packet);
        pcm.get(sender).getRegion()
                //                .getSurroundingRegions()
                .getEntities().getPlayers()
                //                .stream().flatMap(r -> r.getPlayers().stream())
                .forEach(e -> sendPacket(e, packet));
    }

    @Override
    public void disconnected(Entity entity) {
        engine.removeEntity(entity);
    }
}
