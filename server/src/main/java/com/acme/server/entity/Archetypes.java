package com.acme.server.entity;

import com.acme.commons.ai.BrainComponent;
import com.acme.commons.ashley.Archetype;
import com.acme.server.component.*;

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
                .add(InventoryComponent.class)
                .add(StatsComponent.class);
    }

    public static final Archetype CREATURE_TYPE = new Archetype(BASE_TYPE);

    static {
        CREATURE_TYPE
                .add(KnownListComponent.class)
                .add(InventoryComponent.class)
                .add(StatsComponent.class)
                .add(DropComponent.class)
                .add(HateComponent.class)
                .add(BrainComponent.class);
    }

    public static final Archetype ITEM_TYPE = new Archetype(BASE_TYPE);

    static {
        ITEM_TYPE
                .add(PickupComponent.class);
    }

    public static final Archetype CHEST_TYPE = new Archetype(BASE_TYPE);

    static {
        CHEST_TYPE
                .add(DropComponent.class);
    }
}
