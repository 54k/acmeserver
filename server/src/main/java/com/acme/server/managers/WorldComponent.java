package com.acme.server.managers;

import com.acme.engine.ecs.core.Component;
import com.acme.server.world.Instance;

public class WorldComponent extends Component {

    private Instance instance;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
