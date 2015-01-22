package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.*;
import com.acme.server.packet.outbound.AttackPacket;
import com.acme.server.packet.outbound.DamagePacket;
import com.acme.server.packet.outbound.KillPacket;
import com.acme.server.system.NetworkSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class CombatManager extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<StatsComponent> scm;
    private ComponentMapper<InventoryComponent> icm;
    private ComponentMapper<TypeComponent> tcm;
    private ComponentMapper<HateListComponent> hcm;

    private StatsManager statsManager;
    private NetworkSystem networkSystem;
    private WorldManager worldManager;
    private DropManager dropManager;
    private HateListManager hateListManager;

    public void engage(long attackerId, Entity target) {
        WorldComponent worldComponent = wcm.get(target);
        Entity attacker = worldComponent.getInstance().findEntity(attackerId);
        engage(attacker, target);
    }

    public void engage(Entity attacker, long targetId) {
        WorldComponent worldComponent = wcm.get(attacker);
        Entity target = worldComponent.getInstance().findEntity(targetId);
        engage(attacker, target);
    }

    public void engage(Entity attacker, Entity target) {
        pcm.get(attacker).setPosition(pcm.get(target).getPosition());
        networkSystem.sendToSelfAndRegion(attacker, new AttackPacket(attacker.getId(), target.getId()));
    }

    public void attack(Entity attacker, long targetId) {
        WorldComponent worldComponent = wcm.get(attacker);
        Entity target = worldComponent.getInstance().findEntity(targetId);
        attack(attacker, target);
    }

    public void attack(long attackerId, Entity target) {
        WorldComponent worldComponent = wcm.get(target);
        Entity attacker = worldComponent.getInstance().findEntity(attackerId);
        attack(attacker, target);
    }

    public void attack(Entity attacker, Entity target) {
        int weaponRank = icm.get(attacker).getWeapon();
        int armorRank = icm.get(target).getArmor();
        int damage = Math.max(1, weaponRank - armorRank);
        statsManager.addHitPoints(target, -(damage));
        networkSystem.sendPacket(attacker, new DamagePacket(target.getId(), damage));
        StatsComponent statsComponent = scm.get(target);
        if (statsComponent.getHitPoints() == 0) {
            dropManager.dropItems(target);
            worldManager.decay(target);
            statsManager.resetHitPoints(target);
            networkSystem.sendPacket(attacker, new KillPacket(tcm.get(target).getType()));
        }
        if (hcm.has(target)) {
            hateListManager.increaseHate(attacker, target, damage);
        }
    }
}
