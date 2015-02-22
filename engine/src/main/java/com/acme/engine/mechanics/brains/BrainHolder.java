package com.acme.engine.mechanics.brains;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.Entity;

public class BrainHolder extends Component {

    Brain<Entity> brain;

    public Brain<Entity> getBrain() {
        return brain;
    }

    public void setBrain(Brain<Entity> brain) {
        this.brain = brain;
    }
}
