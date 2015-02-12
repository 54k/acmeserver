package com.acme.engine.effect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EffectList<E> {

    private final E owner;
    private final List<Effect<E>> effects = new ArrayList<>();

    private boolean updating;
    private final Queue<Operation<E>> operations = new LinkedList<>();

    public EffectList(E owner) {
        this.owner = owner;
    }

    public void apply(Effect<E> effect) {
        if (!stack(effect)) {
            apply0(effect);
        }
    }

    private boolean stack(Effect<E> effect) {
        if (hasEffect(effect)) {
            Effect<E> appliedEffect = effects.get(effects.indexOf(effect));
            return appliedEffect.stack(owner, effect);
        }
        return false;
    }

    private void apply0(Effect<E> effect) {
        if (updating) {
            operations.add(new Operation<>(OperationType.APPLY, effect));
        } else {
            applyImmediate(effect);
        }
    }

    private void applyImmediate(Effect<E> effect) {
        effects.add(effect);
        effect.apply(owner);
    }

    public void update(float deltaTime) {
        updating = true;
        for (Effect<E> effect : effects) {
            effect.update(owner, deltaTime);
        }
        updating = false;
        executeOperations();
    }

    private void executeOperations() {
        while (!operations.isEmpty()) {
            Operation<E> operation = operations.poll();
            switch (operation.operationType) {
                case APPLY:
                    applyImmediate(operation.effect);
                    break;
                case REMOVE:
                    removeImmediate(operation.effect);
                    break;
            }
        }
    }

    public void remove(Effect<E> effect) {
        if (updating) {
            operations.add(new Operation<>(OperationType.REMOVE, effect));
        } else {
            removeImmediate(effect);
        }
    }

    private void removeImmediate(Effect<E> effect) {
        effects.remove(effect);
        effect.remove(owner);
    }

    public boolean hasEffect(Effect<E> effect) {
        return effects.contains(effect);
    }

    private static final class Operation<E> {
        final OperationType operationType;
        final Effect<E> effect;

        Operation(OperationType operationType, Effect<E> effect) {
            this.operationType = operationType;
            this.effect = effect;
        }
    }

    private static enum OperationType {
        APPLY, REMOVE
    }
}
