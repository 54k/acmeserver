package com.acme.server.model.node;

import com.acme.server.model.component.TransformComponent;

public interface TransformNode extends WorldNode {

    TransformComponent getTransform();
}
