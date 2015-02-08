package com.acme.server.component;

import com.acme.server.util.EntityContainer;
import com.badlogic.ashley.core.Component;

public class KnownListComponent extends Component {

    private final EntityContainer entityContainer = new EntityContainer();

    private int distanceToFindObject;
    private int distanceToForgetObject;

    public EntityContainer getKnownEntities() {
        return entityContainer;
    }

    public int getDistanceToFindEntity() {
        return distanceToFindObject;
    }

    public void setDistanceToFindObject(int distanceToFindObject) {
        this.distanceToFindObject = distanceToFindObject;
    }

    public int getDistanceToForgetEntity() {
        return distanceToForgetObject;
    }

    public void setDistanceToForgetObject(int distanceToForgetObject) {
        this.distanceToForgetObject = distanceToForgetObject;
    }
}
