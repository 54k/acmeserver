package com.acme.server.component;

import com.acme.engine.aegis.core.Component;
import com.acme.server.entity.Type;

public class TypeComponent extends Component {

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
