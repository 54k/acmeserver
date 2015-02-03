package com.acme.core.ai;

import com.badlogic.ashley.core.Component;

public class BrainComponent extends Component {

    private Brain brain;

    public Brain getBrain() {
        return brain;
    }

    public void setBrain(Brain brain) {
        this.brain = brain;
    }
}
