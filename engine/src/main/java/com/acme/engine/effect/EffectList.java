package com.acme.engine.effect;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EffectList {

    private final Entity owner;

    private final List<Effect> effects = new ArrayList<>();

    private final Queue<Operation> operations = new LinkedList<>();
    private boolean updating;

    public EffectList(Entity owner) {
        this.owner = owner;
    }

    public void apply(Effect effect) {
        if (updating) {
            operations.add(new Operation(OperationType.APPLY, effect));
        } else {
            apply0(effect);
        }
    }

    private void apply0(Effect effect) {
        effects.add(effect);
        effect.apply(owner);
    }

    public void update(float deltaTime) {
        executeOperations();
        updating = true;
        for (Effect effect : effects) {
            effect.update(owner, deltaTime);
        }
        updating = false;
    }

    private void executeOperations() {
        while (!operations.isEmpty()) {
            Operation operation = operations.poll();
            switch (operation.operationType) {
                case APPLY:
                    apply0(operation.effect);
                    break;
                case REMOVE:
                    remove0(operation.effect);
                    break;
            }
        }
    }

    public void remove(Effect effect) {
        if (updating) {
            operations.add(new Operation(OperationType.REMOVE, effect));
        } else {
            remove0(effect);
        }
    }

    private void remove0(Effect effect) {
        effects.remove(effect);
        effect.remove(owner);
    }

    private static final class Operation {
        OperationType operationType;
        Effect effect;

        Operation(OperationType operationType, Effect effect) {
            this.operationType = operationType;
            this.effect = effect;
        }
    }

    private static enum OperationType {
        APPLY, REMOVE
    }
}
