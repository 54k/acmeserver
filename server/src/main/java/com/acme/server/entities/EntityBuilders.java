package com.acme.server.entities;

import com.acme.engine.ecs.core.EntityBuilder;
import com.acme.engine.mechanics.brains.Brain;
import com.acme.engine.mechanics.timer.SchedulerHolder;
import com.acme.server.combat.Combat;
import com.acme.server.combat.HateList;
import com.acme.server.combat.Stats;
import com.acme.server.inventory.Inventory;
import com.acme.server.inventory.LootTable;
import com.acme.server.inventory.Pickup;
import com.acme.server.managers.PlayerComponent;
import com.acme.server.managers.WorldComponent;
import com.acme.server.position.KnownList;
import com.acme.server.position.Transform;

public final class EntityBuilders {
    private EntityBuilders() {
    }

    public static final EntityBuilder BASE_TYPE = new EntityBuilder();

    static {
        BASE_TYPE
                .addComponentType(Transform.class)
                .addComponentType(WorldComponent.class)
                .addComponentType(EntityType.class)
                .addComponentType(SchedulerHolder.class);
    }

    public static final EntityBuilder PLAYER_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        PLAYER_TYPE
                .addComponentType(PlayerComponent.class)
                .addComponentType(KnownList.class)
                .addComponentType(Inventory.class)
                .addComponentType(Stats.class);
    }

    public static final EntityBuilder CREATURE_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        CREATURE_TYPE
                .addComponentType(KnownList.class)
                .addComponentType(Inventory.class)
                .addComponentType(Stats.class)
                .addComponentType(LootTable.class)
                .addComponentType(HateList.class)
                .addComponentType(Brain.class)
                .addComponentType(Combat.class);
    }

    public static final EntityBuilder ITEM_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        ITEM_TYPE
                .addComponentType(Pickup.class);
    }

    public static final EntityBuilder CHEST_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        CHEST_TYPE
                .addComponentType(LootTable.class);
    }
}
