package com.acme.engine.brain;

import com.acme.engine.aegis.core.Component;
import com.acme.engine.aegis.core.Entity;

public class BrainComponent extends Component {

    private Brain<Entity> brain;

    public Brain<Entity> getBrain() {
        return brain;
    }

    public void setBrain(Brain<Entity> brain) {
        this.brain = brain;
    }
}
