package com.acme.engine.ashley.component;

import com.acme.engine.brain.Brain;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class BrainComponent extends Component {

    private Brain<Entity> brain;

    public Brain<Entity> getBrain() {
        return brain;
    }

    public void setBrain(Brain<Entity> brain) {
        this.brain = brain;
    }
}
