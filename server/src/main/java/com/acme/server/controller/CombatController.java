package com.acme.server.controller;

import com.acme.engine.ashley.ManagerSystem;
import com.acme.engine.ashley.Wired;
import com.acme.server.event.CombatControllerEvent;
import com.acme.server.manager.EntityManager;
import com.acme.server.manager.WorldManager;
import com.acme.server.packet.outbound.AttackPacket;
import com.acme.server.packet.outbound.DamagePacket;
import com.acme.server.packet.outbound.KillPacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.util.Rnd;
import com.acme.server.util.TypeUtils;
import com.acme.server.world.Position;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatController extends ManagerSystem {

    private PositionController positionController;
    private StatsController statsController;
    private InventoryController inventoryController;

    private EntityManager entityManager;
    private WorldManager worldManager;

    private PacketSystem packetSystem;

    public void engage(Entity attacker, Entity target) {
        Position targetPosition = positionController.getPosition(target);
        positionController.updatePosition(attacker, targetPosition);
        packetSystem.sendToSelfAndRegion(attacker, new AttackPacket(attacker.getId(), target.getId()));
    }

    public void attack(Entity attacker, Entity target) {
        int damage = calculateDamage(attacker, target);
        applyDamage(attacker, target, damage);
        post(CombatControllerEvent.class).onEntityDamaged(attacker, target, damage);
        if (statsController.isDead(target)) {
            post(CombatControllerEvent.class).onEntityKilled(attacker, target);
            worldManager.decay(target);
            packetSystem.sendPacket(attacker, new KillPacket(entityManager.getType(target)));
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
        packetSystem.sendPacket(attacker, new DamagePacket(target.getId(), damage));
    }
}
