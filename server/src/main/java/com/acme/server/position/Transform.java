package com.acme.server.position;

import com.acme.ecs.core.Component;
import com.acme.server.world.Orientation;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

public class Transform extends Component {

    public final Position position = new Position();
    public Orientation orientation = Orientation.BOTTOM;
    public Region region;
    // TODO migrate this to spawn component
    public boolean spawned;

    public int getX() {
        return position.getX();
    }

    public void setX(int x) {
        position.setX(x);
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
