package com.acme.server.manager;

import com.acme.engine.brain.Brain;
import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.component.BrainComponent;
import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.server.ai.PatrolBrainState;
import com.acme.server.component.*;
import com.acme.server.entity.Archetypes;
import com.acme.server.entity.Type;
import com.acme.server.template.CreatureTemplate;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Wired
public class EntityManager extends ManagerSystem {

    private ComponentMapper<InventoryComponent> icm;
    private ComponentMapper<StatsComponent> scm;
    private ComponentMapper<TypeComponent> tcm;
    private ComponentMapper<PickupComponent> pcm;
    private ComponentMapper<DropComponent> dcm;
    private ComponentMapper<BrainComponent> bcm;

    private Engine engine;

    private final Map<Type, CreatureTemplate> creaturesByType;

    public EntityManager(Map<Type, CreatureTemplate> creaturesByType) {
        this.creaturesByType = creaturesByType;
    }

    public Type getType(Entity entity) {
        return tcm.get(entity).getType();
    }

    public Entity createPlayer() {
        return create(Type.WARRIOR);
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
        tcm.get(entity).setType(type);
        PickupComponent pickupComponent = pcm.get(entity);
        switch (type) {
            case BURGER:
                pickupComponent.setPickupType(PickupComponent.PickupType.HEALTH_POTION);
                pickupComponent.setAmount(30);
                break;
            case CAKE:
                pickupComponent.setPickupType(PickupComponent.PickupType.HEALTH_POTION);
                pickupComponent.setAmount(20);
                break;
            case FLASK:
                pickupComponent.setPickupType(PickupComponent.PickupType.HEALTH_POTION);
                pickupComponent.setAmount(10);
                break;
            case FIREPOTION:
                pickupComponent.setPickupType(PickupComponent.PickupType.FIREFOX_POTION);
                pickupComponent.setAmount(10 * 1000);
                break;
            case SWORD1:
            case SWORD2:
            case REDSWORD:
            case GOLDENSWORD:
            case MORNINGSTAR:
            case AXE:
            case BLUESWORD:
                pickupComponent.setPickupType(PickupComponent.PickupType.WEAPON);
                break;
            case FIREFOX:
            case CLOTHARMOR:
            case LEATHERARMOR:
            case MAILARMOR:
            case PLATEARMOR:
            case REDARMOR:
            case GOLDENARMOR:
                pickupComponent.setPickupType(PickupComponent.PickupType.ARMOR);
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
        InventoryComponent inventoryComponent = icm.get(entity);
        inventoryComponent.setArmor(creatureTemplate.getArmor());
        inventoryComponent.setWeapon(creatureTemplate.getWeapon());
        StatsComponent statsComponent = scm.get(entity);
        int hitPoints = creatureTemplate.getHitPoints();
        statsComponent.setHitPoints(hitPoints);
        statsComponent.setMaxHitPoints(hitPoints);
        tcm.get(entity).setType(type);
        DropComponent dropComponent = dcm.get(entity);
        List<DropComponent.Drop> drops = creatureTemplate.getDrops().entrySet().stream()
                .map(e -> new DropComponent.Drop(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dropComponent.getDrops().addAll(drops);
        bcm.get(entity).setBrain(new Brain(entity, engine.getSystem(PatrolBrainState.class)));

        engine.addEntity(entity);
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
        tcm.get(entity).setType(type);
        engine.addEntity(entity);
        return entity;
    }
}
