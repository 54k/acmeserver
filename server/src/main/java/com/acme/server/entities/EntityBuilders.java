package com.acme.server.entities;

import com.acme.ecs.entities.EntityBuilder;
import com.acme.commons.brains.Brain;
import com.acme.server.combat.Combat;
import com.acme.server.combat.HateList;
import com.acme.server.combat.Stats;
import com.acme.server.inventory.Inventory;
import com.acme.server.inventory.LootTable;
import com.acme.server.inventory.Pickup;
import com.acme.server.items.Armor;
import com.acme.server.items.Consumable;
import com.acme.server.items.Weapon;
import com.acme.server.managers.PlayerComponent;
import com.acme.server.managers.WorldTransform;
import com.acme.server.position.KnownList;
import com.acme.server.position.Transform;

public final class EntityBuilders {
    private EntityBuilders() {
    }

    public static final EntityBuilder BASE_TYPE = new EntityBuilder();

    static {
        BASE_TYPE.add(Transform.class);
        BASE_TYPE.add(WorldTransform.class);
        BASE_TYPE.add(EntityType.class);
    }

    public static final EntityBuilder PLAYER_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        PLAYER_TYPE.add(PlayerComponent.class);
        PLAYER_TYPE.add(KnownList.class);
        PLAYER_TYPE.add(Inventory.class);
        PLAYER_TYPE.add(Stats.class);
        PLAYER_TYPE.add(Combat.class);
    }

    public static final EntityBuilder CREATURE_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        CREATURE_TYPE.add(KnownList.class);
        CREATURE_TYPE.add(Inventory.class);
        CREATURE_TYPE.add(Stats.class);
        CREATURE_TYPE.add(LootTable.class);
        CREATURE_TYPE.add(HateList.class);
        CREATURE_TYPE.add(Brain.class);
        CREATURE_TYPE.add(Combat.class);
    }

    public static final EntityBuilder ITEM_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        ITEM_TYPE.add(Pickup.class);
    }

    public static final EntityBuilder CONSUMABLE_TYPE = new EntityBuilder(ITEM_TYPE);

    static {
        CONSUMABLE_TYPE.add(Consumable.class);
    }

    public static final EntityBuilder WEAPON_TYPE = new EntityBuilder(ITEM_TYPE);

    static {
        WEAPON_TYPE.add(Weapon.class);
    }

    public static final EntityBuilder ARMOR_TYPE = new EntityBuilder(ITEM_TYPE);

    static {
        ARMOR_TYPE.add(Armor.class);
    }

    public static final EntityBuilder CHEST_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        CHEST_TYPE.add(LootTable.class);
    }
}
