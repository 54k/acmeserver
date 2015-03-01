package com.acme.engine.mechanics.brains;

import com.acme.engine.ecs.core.Component;
import com.acme.engine.ecs.core.Entity;

public class Brain extends Component {

    BrainStateMachine<Entity> brainStateMachine;

    public BrainStateMachine<Entity> getBrainStateMachine() {
        return brainStateMachine;
    }

    public void setBrainStateMachine(BrainStateMachine<Entity> brainStateMachine) {
        this.brainStateMachine = brainStateMachine;
    }
}
