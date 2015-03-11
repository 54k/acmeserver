package com.acme.server.combat;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.entities.EntityFactory;
import com.acme.server.inventory.InventorySystem;
import com.acme.server.model.component.TransformComponent;
import com.acme.server.model.node.TransformNode;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.system.PositionSystem;
import com.acme.server.model.system.WorldSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.AttackPacket;
import com.acme.server.packets.outbound.DamagePacket;
import com.acme.server.packets.outbound.KillPacket;
import com.acme.server.utils.Rnd;
import com.acme.server.utils.TypeUtils;
import com.acme.server.world.Position;

@Wire
public class CombatSystem extends PassiveSystem {

    private ComponentMapper<Combat> combatCm;
    private PositionSystem positionSystem;
    private StatsSystem statsSystem;
    private InventorySystem inventorySystem;
    private EntityFactory entityFactory;
    private WorldSystem worldSystem;
    private PacketSystem packetSystem;

    public void setTarget(Entity entity, Entity target) {
        combatCm.get(entity).target = target;
    }

    public Entity getTarget(Entity entity) {
        return combatCm.get(entity).target;
    }

    public void engage(Entity attacker, Entity target) {
        Position targetPosition = target.getComponent(TransformComponent.class).position;
        positionSystem.teleportTo(attacker.getNode(TransformNode.class), targetPosition);
        packetSystem.sendToSelfAndRegion(attacker, new AttackPacket(attacker.getId(), target.getId()));
    }

    public void attack(Entity attacker, Entity target) {
        int damage = calculateDamage(attacker, target);
        applyDamage(attacker, target, damage);
        event(CombatListener.class).dispatch().onEntityDamaged(attacker, target, damage);
        if (statsSystem.isDead(target)) {
            event(CombatListener.class).dispatch().onEntityKilled(attacker, target);
            setTarget(target, null);
            worldSystem.decay(target.getNode(WorldNode.class));
            packetSystem.sendPacket(attacker, new KillPacket(entityFactory.getType(target)));
        }
    }

    // TODO implement formulas
    private int calculateDamage(Entity attacker, Entity target) {
        int weapon = inventorySystem.getEquippedWeapon(attacker);
        int armor = inventorySystem.getEquippedArmor(target);
        int damage;
        if (TypeUtils.isCreature(attacker)) {
            damage = Rnd.between(1, 5);
        } else {
            damage = Math.max(1, weapon - armor);
        }
        return damage;
    }

    private void applyDamage(Entity attacker, Entity target, int damage) {
        statsSystem.addHitPoints(target, -damage);
        packetSystem.sendPacket(attacker, new DamagePacket(target.getId(), damage));
    }
}
