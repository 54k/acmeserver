package com.acme.server.model.node;

import com.acme.server.model.component.PositionComponent;

public interface PositionNode extends WorldNode {

    PositionComponent getPosition();
}
