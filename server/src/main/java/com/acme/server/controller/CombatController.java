package com.acme.server.controller;

import com.acme.commons.ashley.EntityEngine;
import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.*;
import com.acme.server.event.CombatEvents;
import com.acme.server.manager.PositionManager;
import com.acme.server.manager.StatsManager;
import com.acme.server.manager.WorldManager;
import com.acme.server.packet.outbound.AttackPacket;
import com.acme.server.packet.outbound.DamagePacket;
import com.acme.server.packet.outbound.KillPacket;
import com.acme.server.system.NetworkSystem;
import com.acme.server.util.Rnd;
import com.acme.server.util.TypeUtils;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatController extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<StatsComponent> scm;
    private ComponentMapper<InventoryComponent> icm;
    private ComponentMapper<TypeComponent> tcm;
    private ComponentMapper<HateComponent> hcm;

    private EntityEngine engine;
    private StatsManager statsManager;
    private NetworkSystem networkSystem;
    private WorldManager worldManager;
    private PositionManager positionManager;

    public void engage(long attackerId, Entity target) {
        WorldComponent worldComponent = wcm.get(target);
        Entity attacker = worldComponent.getInstance().findEntityById(attackerId);
        engage(attacker, target);
    }

    public void engage(Entity attacker, long targetId) {
        WorldComponent worldComponent = wcm.get(attacker);
        Entity target = worldComponent.getInstance().findEntityById(targetId);
        engage(attacker, target);
    }

    public void engage(Entity attacker, Entity target) {
        Position targetPosition = pcm.get(target).getPosition();
        positionManager.updatePosition(attacker, targetPosition);
        networkSystem.sendToSelfAndRegion(attacker, new AttackPacket(attacker.getId(), target.getId()));
    }

    public void attack(Entity attacker, long targetId) {
        WorldComponent worldComponent = wcm.get(attacker);
        Entity target = worldComponent.getInstance().findEntityById(targetId);
        attack(attacker, target);
    }

    public void attack(long attackerId, Entity target) {
        WorldComponent worldComponent = wcm.get(target);
        Entity attacker = worldComponent.getInstance().findEntityById(attackerId);
        attack(attacker, target);
    }

    public void attack(Entity attacker, Entity target) {
        int weaponRank = icm.get(attacker).getWeapon();
        int armorRank = icm.get(target).getArmor();

        int damage;
        if (TypeUtils.isCreature(attacker)) {
            damage = Rnd.between(1, 5);
        } else {
            damage = Math.max(1, weaponRank - armorRank);
        }

        statsManager.addHitPoints(target, -damage);
        networkSystem.sendPacket(attacker, new DamagePacket(target.getId(), damage));
        StatsComponent statsComponent = scm.get(target);
        engine.post(CombatEvents.class).onEntityDamaged(attacker, target, damage);
        if (statsComponent.getHitPoints() == 0) {
            worldManager.decay(target);
            statsManager.resetHitPoints(target);
            post(CombatEvents.class).onEntityKilled(attacker, target);
            networkSystem.sendPacket(attacker, new KillPacket(tcm.get(target).getType()));
        }
    }
}
