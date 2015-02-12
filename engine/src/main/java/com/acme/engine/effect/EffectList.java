package com.acme.engine.effect;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

public class EffectList {

    private final Entity owner;

    private final List<Effect> effects = new ArrayList<>();

    public EffectList(Entity owner) {
        this.owner = owner;
    }

    public void apply(Effect effect) {
        effects.add(effect);
        effect.apply(owner);
    }

    public void update(float deltaTime) {
        ArrayList<Effect> effects = new ArrayList<>(this.effects);
        for (Effect effect : effects) {
            effect.update(owner, deltaTime);
        }
    }

    public void remove(Effect effect) {
        effects.remove(effect);
        effect.remove(owner);
    }
}
