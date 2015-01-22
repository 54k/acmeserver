package com.acme.server.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public class HateListComponent extends Component {

    private Entity target;
    private final Map<Entity, Integer> attackers = new HashMap<>();

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public Map<Entity, Integer> getAttackers() {
        return attackers;
    }
}
