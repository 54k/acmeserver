package com.acme.server.entity;

import com.acme.engine.ashley.Archetype;
import com.acme.engine.brain.BrainComponent;
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

    public static final Archetype BASE_TYPE = new Archetype();

    static {
        BASE_TYPE
                .add(PositionComponent.class)
                .add(WorldComponent.class)
                .add(TypeComponent.class);
    }

    public static final Archetype PLAYER_TYPE = new Archetype(BASE_TYPE);

    static {
        PLAYER_TYPE
                .add(PlayerComponent.class)
                .add(KnownListComponent.class)
                .add(Inventory.class)
                .add(Stats.class);
    }

    public static final Archetype CREATURE_TYPE = new Archetype(BASE_TYPE);

    static {
        CREATURE_TYPE
                .add(KnownListComponent.class)
                .add(Inventory.class)
                .add(Stats.class)
                .add(DropList.class)
                .add(HateList.class)
                .add(BrainComponent.class)
                .add(Combat.class);
    }

    public static final Archetype ITEM_TYPE = new Archetype(BASE_TYPE);

    static {
        ITEM_TYPE
                .add(Pickup.class);

    }

    public static final Archetype CHEST_TYPE = new Archetype(BASE_TYPE);

    static {
        CHEST_TYPE
                .add(DropList.class);
    }
}
