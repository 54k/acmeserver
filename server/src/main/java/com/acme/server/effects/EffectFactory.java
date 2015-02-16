package com.acme.server.effects;

import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.engine.effects.Effect;
import com.badlogic.ashley.core.Entity;

public final class EffectFactory extends ManagerSystem {

    public static String GLOBAL_REGEN_EFFECT = "global_regen_effect";
    public static String HEALTH_POTION_EFFECT = "health_potion_effect";
    public static String FIREFOX_POTION_EFFECT = "firefox_potion_effect";

    public Entity createGlobalRegenEffect() {
        return new Entity()
                .add(new Effect(GLOBAL_REGEN_EFFECT, -1, 5000))
                .add(new RegenImpact());
    }

    public Entity createHealthPotionEffect(int amount) {
        return new Entity()
                .add(new Effect(HEALTH_POTION_EFFECT, 0, 0))
                .add(new HealImpact(amount));
    }

    public Entity createFireFoxPotionEffect(float duration) {
        return new Entity()
                .add(new Effect(FIREFOX_POTION_EFFECT, 1, duration))
                .add(new InvulImpact());
    }
}
