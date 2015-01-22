package com.acme.server.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

public class KnownListComponent extends Component {

    private final List<Entity> knownPlayers = new ArrayList<>();
    private final List<Entity> knownObjects = new ArrayList<>();

    private int distanceToFindObject;
    private int distanceToForgetObject;

    public List<Entity> getKnownPlayers() {
        return knownPlayers;
    }

    public List<Entity> getKnownEntities() {
        return knownObjects;
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
