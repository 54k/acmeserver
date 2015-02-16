package com.acme.server.effects;

import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.engine.effects.Effect;
import com.badlogic.ashley.core.Entity;

@Wired
public final class EffectFactory extends ManagerSystem {

    public static String GLOBAL_REGEN = "global_regen";
    public static String INVUL_FIREFOX = "invul_firefox";

    private EntityEngine engine;

    public Entity createRegenEffect() {
        Entity effect = new Entity()
                .add(new Effect(GLOBAL_REGEN, -1, 1000))
                .add(new RegenImpact());
        engine.addEntity(effect);
        return effect;
    }

    public Entity createInvulEffect(float duration) {
        Entity effect = new Entity()
                .add(new Effect(INVUL_FIREFOX, 0, duration))
                .add(new InvulImpact());
        engine.addEntity(effect);
        return effect;
    }
}
