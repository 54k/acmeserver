package com.acme.server.entities;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.engine.mechanics.brains.Brain;
import com.acme.engine.mechanics.brains.BrainStateMachine;
import com.acme.server.brains.CombatState;
import com.acme.server.brains.GlobalState;
import com.acme.server.brains.PatrolState;
import com.acme.server.combat.StatsSystem;
import com.acme.server.impacts.RegenImpact;
import com.acme.server.inventory.Inventory;
import com.acme.server.inventory.LootTable;
import com.acme.server.inventory.Pickup;
import com.acme.server.templates.CreatureTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Wire
public final class EntityFactory extends PassiveSystem {

    private ComponentMapper<Inventory> inventoryCm;
    private ComponentMapper<EntityType> typeCm;
    private ComponentMapper<Pickup> pickupCm;
    private ComponentMapper<LootTable> dropListCm;
    private ComponentMapper<Brain> brainCm;

    private Engine engine;
    private StatsSystem statsSystem;

    private final Map<Type, CreatureTemplate> creaturesByType;

    public EntityFactory(Map<Type, CreatureTemplate> creaturesByType) {
        this.creaturesByType = creaturesByType;
    }

    public Type getType(Entity entity) {
        return typeCm.get(entity).getType();
    }

    public Entity createPlayer() {
        Entity entity = create(Type.WARRIOR);
        entity.addComponent(new RegenImpact());
        engine.addEntity(entity);
        return entity;
    }

    public Entity createEntity(Type type) {
        if (type.getEntityBuilder() == EntityBuilders.CREATURE_TYPE) {
            return createCreature(type);
        } else if (type.getEntityBuilder() == EntityBuilders.ITEM_TYPE) {
            return createItem(type);
        } else {
            Entity entity = create(type);
            engine.addEntity(entity);
            return entity;
        }
    }

    private Entity createItem(Type type) {
        Entity entity = create(type);
        typeCm.get(entity).setType(type);
        Pickup pickup = pickupCm.get(entity);
        switch (type) {
            case BURGER:
                pickup.setPickupType(Pickup.PickupType.HEALTH_POTION);
                pickup.setAmount(30);
                break;
            case CAKE:
                pickup.setPickupType(Pickup.PickupType.HEALTH_POTION);
                pickup.setAmount(20);
                break;
            case FLASK:
                pickup.setPickupType(Pickup.PickupType.HEALTH_POTION);
                pickup.setAmount(10);
                break;
            case FIREPOTION:
                pickup.setPickupType(Pickup.PickupType.FIREFOX_POTION);
                pickup.setAmount(10000);
                break;
            case SWORD1:
            case SWORD2:
            case REDSWORD:
            case GOLDENSWORD:
            case MORNINGSTAR:
            case AXE:
            case BLUESWORD:
                pickup.setPickupType(Pickup.PickupType.WEAPON);
                break;
            case FIREFOX:
            case CLOTHARMOR:
            case LEATHERARMOR:
            case MAILARMOR:
            case PLATEARMOR:
            case REDARMOR:
            case GOLDENARMOR:
                pickup.setPickupType(Pickup.PickupType.ARMOR);
                break;
            default:
                throw new IllegalArgumentException("Cannot set pickupType for instanceType " + type);
        }
        engine.addEntity(entity);
        return entity;
    }

    private Entity createCreature(Type type) {
        CreatureTemplate creatureTemplate = getCreatureTemplate(type);
        Entity entity = create(type);
        Inventory inventory = inventoryCm.get(entity);
        inventory.setArmor(creatureTemplate.getArmor());
        inventory.setWeapon(creatureTemplate.getWeapon());
        statsSystem.setMaxHitPointsAndReset(entity, creatureTemplate.getHitPoints());
        typeCm.get(entity).setType(type);
        LootTable lootTable = dropListCm.get(entity);
        List<LootTable.LootEntry> lootEntries = creatureTemplate.getDrops().entrySet().stream()
                .map(e -> new LootTable.LootEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        lootTable.getLootEntries().addAll(lootEntries);

        applyBrain(entity);

        entity.addComponent(new RegenImpact());
        engine.addEntity(entity);
        return entity;
    }

    private void applyBrain(Entity entity) {
        Brain brain = brainCm.get(entity);
        BrainStateMachine<Entity> brainStateMachine = new BrainStateMachine<>(entity);
        GlobalState globalState = new GlobalState();
        engine.processObject(globalState);
        PatrolState patrolState = new PatrolState();
        engine.processObject(patrolState);
        CombatState combatState = new CombatState();
        engine.processObject(combatState);

        brainStateMachine.addState(patrolState);
        brainStateMachine.addState(combatState);
        brain.setBrainStateMachine(brainStateMachine);
        brainStateMachine.setGlobalState(globalState);
    }

    private CreatureTemplate getCreatureTemplate(Type type) {
        CreatureTemplate creatureTemplate = creaturesByType.get(type);
        if (creatureTemplate == null) {
            throw new RuntimeException("Creature template not found for " + type);
        }
        return creatureTemplate;
    }

    private Entity create(Type type) {
        Entity entity = type.getEntityBuilder().get();
        typeCm.get(entity).setType(type);
        return entity;
    }
}
