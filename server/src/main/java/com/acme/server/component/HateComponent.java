package com.acme.server.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public class HateComponent extends Component {

    private Entity target;
    private final Map<Entity, Integer> haters = new HashMap<>();

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public Map<Entity, Integer> getHaters() {
        return haters;
    }

    public Entity getMostHated() {
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
