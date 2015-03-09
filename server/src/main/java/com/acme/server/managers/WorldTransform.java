package com.acme.server.managers;

import com.acme.ecs.core.Component;
import com.acme.server.world.Instance;
import com.acme.server.world.Region;

public class WorldTransform extends Component {

    public Instance instance;
    public Region region;
    public boolean spawned;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
