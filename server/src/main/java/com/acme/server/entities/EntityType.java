package com.acme.server.entities;

import com.acme.engine.ecs.core.Component;

public class EntityType extends Component {

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
