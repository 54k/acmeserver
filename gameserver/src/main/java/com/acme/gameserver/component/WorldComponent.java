package com.acme.gameserver.component;

import com.acme.gameserver.world.Instance;
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
