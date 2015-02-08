package com.acme.server.manager;

import com.acme.engine.application.Context;
import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.server.component.*;
import com.acme.server.controller.PositionController;
import com.acme.server.packet.outbound.HitPointsPacket;
import com.acme.server.packet.outbound.WelcomePacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.util.PositionUtils;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Instance;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Collection;

@Wired
public class LoginManager extends ManagerSystem {

    private ComponentMapper<InventoryComponent> icm;
    private ComponentMapper<PlayerComponent> pcm;
    private ComponentMapper<PositionComponent> poscm;
    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<KnownListComponent> kcm;
    private ComponentMapper<StatsComponent> scm;

    private Context context;

    private PositionController positionController;
    private WorldManager worldManager;
    private PacketSystem packetSystem;

    public void login(Entity entity, String name, int weapon, int armor) {
        PlayerComponent playerComponent = pcm.get(entity);
        playerComponent.setName(name);
        playerComponent.setState(PlayerComponent.State.PLAYING);

        PositionComponent positionComponent = poscm.get(entity);

        Collection<Area> startingAreas = worldManager.getWorld().getPlayerStartingAreas().values();
        int i = Rnd.between(0, startingAreas.size() - 1);
        Area area = startingAreas.stream().skip(i).findFirst().get();
        Position position = PositionUtils.getRandomPositionInside(area);

        positionComponent.setPosition(position);
        positionComponent.setOrientation(Orientation.BOTTOM);

        WorldComponent worldComponent = wcm.get(entity);
        Instance instance = worldManager.getAvailableInstance();
        worldComponent.setInstance(instance);

        KnownListComponent knownListComponent = kcm.get(entity);
        knownListComponent.setDistanceToFindObject(60);
        knownListComponent.setDistanceToForgetObject(60);
        InventoryComponent inventoryComponent = icm.get(entity);
        inventoryComponent.setWeapon(weapon);
        inventoryComponent.setArmor(armor);

        StatsComponent statsComponent = scm.get(entity);
        int hitPoints = 60;
        statsComponent.setHitPoints(hitPoints);
        statsComponent.setMaxHitPoints(hitPoints);

        context.schedule(() -> spawnPlayer(entity, name, position, hitPoints));
    }

    private void spawnPlayer(Entity entity, String name, Position position, int hitPoints) {
        if (worldManager.getPlayerById(entity.getId()) == null) {
            worldManager.bringIntoWorld(entity);
        }
        worldManager.spawn(entity);
        WelcomePacket welcomePacket = new WelcomePacket(entity.getId(), name, position.getX(), position.getY(), 0);
        packetSystem.sendPacket(entity, welcomePacket);
        packetSystem.sendPacket(entity, new HitPointsPacket(hitPoints));
    }
}
