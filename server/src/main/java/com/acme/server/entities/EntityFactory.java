package com.acme.server.entities;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.component.BrainComponent;
import com.acme.engine.ashley.component.EffectListComponent;
import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.engine.brain.Brain;
import com.acme.engine.effect.EffectList;
import com.acme.engine.effects.EffectSystem;
import com.acme.server.brain.PatrolBrainState;
import com.acme.server.component.DropComponent;
import com.acme.server.component.InventoryComponent;
import com.acme.server.component.StatsComponent;
import com.acme.server.component.TypeComponent;
import com.acme.server.effects.EffectFactory;
import com.acme.server.pickups.Pickup;
import com.acme.server.template.CreatureTemplate;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Wired
public final class EntityFactory extends ManagerSystem {

    private ComponentMapper<InventoryComponent> inventoryCm;
    private ComponentMapper<StatsComponent> statsCm;
    private ComponentMapper<TypeComponent> typeCm;
    private ComponentMapper<Pickup> pickupCm;
    private ComponentMapper<DropComponent> dropCm;
    private ComponentMapper<BrainComponent> brainCm;
    private ComponentMapper<EffectListComponent> effectListCm;

    private Engine engine;

    private EffectFactory effectFactory;
    private EffectSystem effectSystem;

    private final Map<Type, CreatureTemplate> creaturesByType;

    public EntityFactory(Map<Type, CreatureTemplate> creaturesByType) {
        this.creaturesByType = creaturesByType;
    }

    public Type getType(Entity entity) {
        return typeCm.get(entity).getType();
    }

    public Entity createPlayer() {
        Entity entity = create(Type.WARRIOR);
        effectListCm.get(entity).setEffectList(new EffectList<>(entity));
        effectSystem.applyEffect(effectFactory.createGlobalRegenEffect(), entity);
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
        effectListCm.get(entity).setEffectList(new EffectList<>(entity));
        return entity;
    }

    private Entity createCreature(Type type) {
        CreatureTemplate creatureTemplate = getCreatureTemplate(type);
        Entity entity = create(type);
        InventoryComponent inventoryComponent = inventoryCm.get(entity);
        inventoryComponent.setArmor(creatureTemplate.getArmor());
        inventoryComponent.setWeapon(creatureTemplate.getWeapon());
        StatsComponent statsComponent = statsCm.get(entity);
        int hitPoints = creatureTemplate.getHitPoints();
        statsComponent.setHitPoints(hitPoints);
        statsComponent.setMaxHitPoints(hitPoints);
        typeCm.get(entity).setType(type);
        DropComponent dropComponent = dropCm.get(entity);
        List<DropComponent.Drop> drops = creatureTemplate.getDrops().entrySet().stream()
                .map(e -> new DropComponent.Drop(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dropComponent.getDrops().addAll(drops);
        brainCm.get(entity).setBrain(new Brain<>(entity, engine.getSystem(PatrolBrainState.class)));
        effectListCm.get(entity).setEffectList(new EffectList<>(entity));
        effectSystem.applyEffect(effectFactory.createGlobalRegenEffect(), entity);
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
        Entity entity = type.getArchetype().build();
        typeCm.get(entity).setType(type);
        engine.addEntity(entity);
        return entity;
    }
}
