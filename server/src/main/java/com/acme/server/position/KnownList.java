package com.acme.server.position;

import com.acme.ecs.core.Component;
import com.acme.server.utils.EntityContainer;

public class KnownList extends Component {

    public final EntityContainer entityContainer;
    public int distanceToFindObject;
    public int distanceToForgetObject;

    public KnownList() {
        this(-1, -1);
    }

    public KnownList(int distanceToFindObject, int distanceToForgetObject) {
        entityContainer = new EntityContainer();
        this.distanceToFindObject = distanceToFindObject;
        this.distanceToForgetObject = distanceToForgetObject;
    }

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
