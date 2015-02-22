package com.acme.server.entity;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.engine.mechanics.brain.Brain;
import com.acme.engine.mechanics.brain.BrainComponent;
import com.acme.server.brain.PatrolBrainState;
import com.acme.server.combat.StatsController;
import com.acme.server.component.TypeComponent;
import com.acme.server.impact.RegenImpact;
import com.acme.server.inventory.DropList;
import com.acme.server.inventory.Inventory;
import com.acme.server.pickup.Pickup;
import com.acme.server.template.CreatureTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Wire
public final class EntityFactory extends PassiveSystem {

    private ComponentMapper<Inventory> inventoryCm;
    private ComponentMapper<TypeComponent> typeCm;
    private ComponentMapper<Pickup> pickupCm;
    private ComponentMapper<DropList> dropListCm;
    private ComponentMapper<BrainComponent> brainCm;

    private Engine engine;
    private StatsController statsController;

    private final Map<Type, CreatureTemplate> creaturesByType;

    public EntityFactory(Map<Type, CreatureTemplate> creaturesByType) {
        this.creaturesByType = creaturesByType;
    }

    public Type getType(Entity entity) {
        return typeCm.get(entity).getType();
    }

    public Entity createPlayer() {
        Entity entity = create(Type.WARRIOR);
        entity.add(new RegenImpact());
        return entity;
    }

    public Entity createEntity(Type type) {
        if (type.getArchetype() == Archetypes.CREATURE_TYPE) {
            return createCreature(type);
        } else if (type.getArchetype() == Archetypes.ITEM_TYPE) {
            return createItem(type);
        } else {
            return create(type);
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
        return entity;
    }

    private Entity createCreature(Type type) {
        CreatureTemplate creatureTemplate = getCreatureTemplate(type);
        Entity entity = create(type);
        Inventory inventory = inventoryCm.get(entity);
        inventory.setArmor(creatureTemplate.getArmor());
        inventory.setWeapon(creatureTemplate.getWeapon());
        statsController.setMaxHitPointsAndReset(entity, creatureTemplate.getHitPoints());
        typeCm.get(entity).setType(type);
        DropList dropList = dropListCm.get(entity);
        List<DropList.Drop> drops = creatureTemplate.getDrops().entrySet().stream()
                .map(e -> new DropList.Drop(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dropList.getDrops().addAll(drops);
        brainCm.get(entity).setBrain(new Brain<>(entity, engine.getSystem(PatrolBrainState.class)));
        entity.add(new RegenImpact());
        return entity;
    }

    private CreatureTemplate getCreatureTemplate(Type type) {
        CreatureTemplate creatureTemplate = creaturesByType.get(type);
        if (creatureTemplate == null) {
            throw new RuntimeException("Creature template not found for " + type);
        }
        return creatureTemplate;
    }

    private Entity create(Type type) {
        Entity entity = type.getArchetype().buildEntity();
        typeCm.get(entity).setType(type);
        engine.addEntity(entity);
        return entity;
    }
}
