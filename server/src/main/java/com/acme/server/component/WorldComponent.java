package com.acme.server.component;

import com.acme.server.world.Instance;
import com.badlogic.ashley.core.Component;

public class WorldComponent extends Component {

    private Instance instance;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
