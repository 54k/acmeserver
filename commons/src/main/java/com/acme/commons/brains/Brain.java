package com.acme.commons.brains;

import com.acme.ecs.core.Component;
import com.acme.ecs.core.Entity;

public class Brain extends Component {

    BrainStateMachine<Entity> brainStateMachine;

    public BrainStateMachine<Entity> getBrainStateMachine() {
        return brainStateMachine;
    }

    public void setBrainStateMachine(BrainStateMachine<Entity> brainStateMachine) {
        this.brainStateMachine = brainStateMachine;
    }
}
