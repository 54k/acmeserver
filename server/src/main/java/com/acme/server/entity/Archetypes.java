package com.acme.server.entity;

import com.acme.engine.ecs.core.EntityBuilder;
import com.acme.engine.mechanics.brain.BrainComponent;
import com.acme.server.combat.Combat;
import com.acme.server.combat.HateList;
import com.acme.server.combat.Stats;
import com.acme.server.component.*;
import com.acme.server.inventory.DropList;
import com.acme.server.inventory.Inventory;
import com.acme.server.pickup.Pickup;

public final class Archetypes {
    private Archetypes() {
    }

    public static final EntityBuilder BASE_TYPE = new EntityBuilder();

    static {
        BASE_TYPE
                .addComponentType(PositionComponent.class)
                .addComponentType(WorldComponent.class)
                .addComponentType(TypeComponent.class);
    }

    public static final EntityBuilder PLAYER_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        PLAYER_TYPE
                .addComponentType(PlayerComponent.class)
                .addComponentType(KnownListComponent.class)
                .addComponentType(Inventory.class)
                .addComponentType(Stats.class);
    }

    public static final EntityBuilder CREATURE_TYPE = new EntityBuilder(BASE_TYPE);

    static {
        CREATURE_TYPE
                .addComponentType(KnownListComponent.class)
                .addComponentType(Inventory.class)
                .addComponentType(Stats.class)
                .addComponentType(DropList.class)
                .addComponentType(HateList.class)
                .addComponentType(BrainComponent.class)
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
                .addComponentType(DropList.class);
    }
}
