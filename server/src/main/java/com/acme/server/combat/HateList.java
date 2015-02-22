package com.acme.server.combat;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.Entity;

import java.util.HashMap;
import java.util.Map;

public final class HateList extends Component {

    final Map<Entity, Integer> haters = new HashMap<>();

    Entity getMostHated() {
        if (haters.isEmpty()) {
            return null;
        }

        return haters.entrySet().stream()
                .max(this::compareHaters)
                .get().getKey();
    }

    private int compareHaters(Map.Entry<Entity, Integer> e1, Map.Entry<Entity, Integer> e2) {
        return e1.getValue() > e2.getValue() ? 1 : e1.getValue().equals(e2.getValue()) ? 0 : -1;
    }
}
