package com.acme.server.packets;

import com.acme.commons.utils.collections.Predicates;
import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Aspect;
import com.acme.ecs.core.Wire;
import com.acme.commons.network.*;
import com.acme.server.entities.EntityBuilders;
import com.acme.server.entities.EntityFactory;
import com.acme.server.packets.inbound.*;
import com.acme.server.position.KnownList;
import com.acme.server.position.Transform;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Wire
public class PacketSystem extends NetworkIteratingSystem {

    private ComponentMapper<SessionComponent> scm;
    private ComponentMapper<KnownList> kcm;
    private ComponentMapper<Transform> pcm;

    private Engine engine;
    private EntityFactory entityFactory;

    private ObjectMapper objectMapper;

    private final PacketReader packetReader;

    public PacketSystem(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
        return entityFactory.createPlayer();
    }

    @Override
    public void connected(Entity entity) {
        scm.get(entity).getSession().write("go");
    }

    @Override
    protected void runPacket(InboundPacket packet) {
        engine.processObject(packet);
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
    protected com.acme.commons.network.PacketReader getPacketReader() {
        return packetReader;
    }

    @Override
    public void sendPacket(Entity receiver, OutboundPacket packet) {
        engine.processObject(packet);
        super.sendPacket(receiver, packet);
    }

    @Override
    public void sendToAll(OutboundPacket packet) {
        engine.processObject(packet);
        super.sendToAll(packet);
    }

    public void sendToSelfAndKnownList(Entity sender, OutboundPacket packet) {
        sendPacket(sender, packet);
        sendToKnownList(sender, packet);
    }
    private static Aspect playerAspect = EntityBuilders.PLAYER_TYPE.getAspect();

    public void sendToKnownList(Entity sender, OutboundPacket packet) {
        kcm.get(sender).getKnownEntities().query(Predicates.aspect(playerAspect)).transform().forEach(e -> sendPacket(e, packet));
    }

    public void sendToSelfAndRegion(Entity sender, OutboundPacket packet) {
        sendPacket(sender, packet);
        pcm.get(sender).getRegion()
                //                .getSurroundingRegions()
                .getEntities().query(Predicates.aspect(playerAspect))
                //                .stream().flatMap(r -> r.getPlayers().stream())
                .forEach(e -> sendPacket(e, packet));
    }

    @Override
    public void disconnected(Entity entity) {
        engine.removeEntity(entity);
    }
}
