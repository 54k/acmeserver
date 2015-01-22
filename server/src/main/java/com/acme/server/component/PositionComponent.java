package com.acme.server.component;

import com.acme.server.world.Orientation;
import com.acme.server.world.Position;
import com.acme.server.world.Region;
import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {

    private final Position position = new Position();
    private Orientation orientation = Orientation.BOTTOM;
    private Region region;
    private boolean spawned;

    public int getX() {
        return position.getX();
    }

    public void setX(int x) {
        position.setX(x);
    }

    public void setXY(int x, int y) {
        position.setXY(x, y);
    }

    public int getY() {
        return position.getY();
    }

    public void setY(int y) {
        position.setY(y);
    }

    public void setPosition(Position position) {
        this.position.setPosition(position);
    }

    public Position getPosition() {
        return position;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }
}
