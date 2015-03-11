package com.acme.server.model.component;

import com.acme.ecs.core.Component;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;

public class TransformComponent extends Component {

    public final Position position = new Position();
    public Orientation orientation = Orientation.BOTTOM;
}
