package com.acme.server.model.node;

import com.acme.server.model.component.KnownListComponent;

public interface KnownListNode extends PositionNode {

    KnownListComponent getKnownList();
}
