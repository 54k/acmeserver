package com.acme.gameserver.controller;

import com.acme.core.ashley.ManagerSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.TypeComponent;
import com.acme.gameserver.event.CombatEvents;
import com.acme.gameserver.manager.WorldManager;
import com.acme.gameserver.packet.outbound.AttackPacket;
import com.acme.gameserver.packet.outbound.DamagePacket;
import com.acme.gameserver.packet.outbound.KillPacket;
import com.acme.gameserver.system.GsPacketSystem;
import com.acme.gameserver.util.Rnd;
import com.acme.gameserver.util.TypeUtils;
import com.acme.gameserver.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatController extends ManagerSystem {

    private ComponentMapper<TypeComponent> tcm;

    private PositionController positionController;
    private StatsController statsController;
    private InventoryController inventoryController;

    private WorldManager worldManager;
    private GsPacketSystem gsPacketSystem;

    public void engage(Entity attacker, Entity target) {
        Position targetPosition = positionController.getPosition(target);
        positionController.updatePosition(attacker, targetPosition);
        gsPacketSystem.sendToSelfAndRegion(attacker, new AttackPacket(attacker.getId(), target.getId()));
    }

    public void attack(Entity attacker, Entity target) {
        int damage = calculateDamage(attacker, target);
        applyDamage(attacker, target, damage);
        post(CombatEvents.class).onEntityDamaged(attacker, target, damage);
        if (statsController.isDead(target)) {
            post(CombatEvents.class).onEntityKilled(attacker, target);
            worldManager.decay(target);
            gsPacketSystem.sendPacket(attacker, new KillPacket(tcm.get(target).getType()));
        }
    }

    private int calculateDamage(Entity attacker, Entity target) {
        int weapon = inventoryController.getEquippedWeapon(attacker);
        int armor = inventoryController.getEquippedArmor(target);
        int damage;
        if (TypeUtils.isCreature(attacker)) {
            damage = Rnd.between(1, 5);
        } else {
            damage = Math.max(1, weapon - armor);
        }
        return damage;
    }

    private void applyDamage(Entity attacker, Entity target, int damage) {
        statsController.addHitPoints(target, -damage);
        gsPacketSystem.sendPacket(attacker, new DamagePacket(target.getId(), damage));
    }
}
