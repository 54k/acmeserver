package com.acme.server.managers;

import com.acme.commons.application.Context;
import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.combat.StatsSystem;
import com.acme.server.inventory.Inventory;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.HitPointsPacket;
import com.acme.server.packets.outbound.WelcomePacket;
import com.acme.server.position.KnownList;
import com.acme.server.position.KnownListSystem;
import com.acme.server.position.PositionSystem;
import com.acme.server.position.Transform;
import com.acme.server.utils.PositionUtils;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;

import java.util.Collection;

@Wire
public class LoginManager extends PassiveSystem {

    private ComponentMapper<Inventory> icm;
    private ComponentMapper<PlayerComponent> pcm;
    private ComponentMapper<Transform> poscm;
    private ComponentMapper<WorldTransform> wcm;
    private ComponentMapper<KnownList> kcm;

    private Context context;

    private PositionSystem positionSystem;
    private StatsSystem statsSystem;
    private WorldManager worldManager;
    private KnownListSystem knownListSystem;
    private PacketSystem packetSystem;

    public void login(Entity entity, String name, int weapon, int armor) {
        PlayerComponent playerComponent = pcm.get(entity);

        if (playerComponent.getState() == PlayerComponent.State.PLAYING) {
            respawnPlayer(entity);
            return;
        }

        playerComponent.setName(name);
        playerComponent.setState(PlayerComponent.State.PLAYING);

        Transform transform = poscm.get(entity);

        Collection<Area> startingAreas = worldManager.getWorld().getPlayerStartingAreas().values();
        int i = Rnd.between(0, startingAreas.size() - 1);
        Area area = startingAreas.stream().skip(i).findFirst().get();
        Position position = PositionUtils.getRandomPositionInside(area);
        playerComponent.setSpawnArea(area);
        transform.setPosition(position);
        transform.setOrientation(Orientation.BOTTOM);

        WorldTransform worldTransform = wcm.get(entity);
        Instance instance = worldManager.getAvailableInstance();
        worldTransform.setInstance(instance);

        Inventory inventory = icm.get(entity);
        inventory.setWeapon(weapon);
        inventory.setArmor(armor);

        int hitPoints = 200;
        statsSystem.setMaxHitPointsAndReset(entity, hitPoints);
        context.schedule(() -> spawnPlayer(entity, name, position, hitPoints));
    }

    private void respawnPlayer(Entity entity) {
        PlayerComponent playerComponent = pcm.get(entity);
        Area spawnArea = playerComponent.getSpawnArea();
        Position position = PositionUtils.getRandomPositionInside(spawnArea);
        poscm.get(entity).setPosition(position);
        statsSystem.resetHitPoints(entity);
        knownListSystem.clearKnownList(entity);
        context.schedule(() -> spawnPlayer(entity, playerComponent.getName(), position, statsSystem.getMaxHitPoints(entity)));
    }

    private void spawnPlayer(Entity entity, String name, Position position, int hitPoints) {
        KnownList knownList = kcm.get(entity);
        knownList.setDistanceToFindObject(100);
        knownList.setDistanceToForgetObject(100);

        if (worldManager.getPlayerById(entity.getId()) == null) {
            worldManager.bringIntoWorld(entity);
        }
        worldManager.spawn(entity);
        WelcomePacket welcomePacket = new WelcomePacket(entity.getId(), name, position.getX(), position.getY(), 0);
        packetSystem.sendPacket(entity, welcomePacket);
        packetSystem.sendPacket(entity, new HitPointsPacket(hitPoints));
    }
}
