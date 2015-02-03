package com.acme.gameserver.component;

import com.acme.gameserver.entity.Type;
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
