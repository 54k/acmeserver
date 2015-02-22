package com.acme.engine.mechanics.brain;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.Entity;

public class BrainComponent extends Component {

    private Brain<Entity> brain;

    public Brain<Entity> getBrain() {
        return brain;
    }

    public void setBrain(Brain<Entity> brain) {
        this.brain = brain;
    }
}
