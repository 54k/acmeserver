package com.acme.server.entities;

import com.acme.engine.ashley.Archetype;
import com.acme.engine.ashley.component.BrainComponent;
import com.acme.engine.ashley.component.EffectListComponent;
import com.acme.engine.effects.EffectList;
import com.acme.server.component.DropComponent;
import com.acme.server.component.HateComponent;
import com.acme.server.component.InventoryComponent;
import com.acme.server.component.KnownListComponent;
import com.acme.server.component.PlayerComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.StatsComponent;
import com.acme.server.component.TypeComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.pickups.Pickup;

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
                .add(StatsComponent.class)
                .add(EffectListComponent.class)
                .add(EffectList.class);
    }

    public static final Archetype CREATURE_TYPE = new Archetype(BASE_TYPE);

    static {
        CREATURE_TYPE
                .add(KnownListComponent.class)
                .add(InventoryComponent.class)
                .add(StatsComponent.class)
                .add(DropComponent.class)
                .add(HateComponent.class)
                .add(BrainComponent.class)
                .add(EffectListComponent.class)
                .add(EffectList.class);
    }

    public static final Archetype ITEM_TYPE = new Archetype(BASE_TYPE);

    static {
        ITEM_TYPE
                .add(Pickup.class)
                .add(EffectListComponent.class);
    }

    public static final Archetype CHEST_TYPE = new Archetype(BASE_TYPE);

    static {
        CHEST_TYPE
                .add(DropComponent.class);
    }
}
