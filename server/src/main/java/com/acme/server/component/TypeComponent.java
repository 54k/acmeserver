package com.acme.server.component;

import com.acme.server.entities.Type;
import com.badlogic.ashley.core.Component;

public class TypeComponent extends Component {

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
