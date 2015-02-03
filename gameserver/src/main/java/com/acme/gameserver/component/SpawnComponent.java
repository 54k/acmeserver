package com.acme.gameserver.component;

import com.acme.gameserver.util.Rnd;
import com.acme.gameserver.world.Area;
import com.acme.gameserver.world.Position;
import com.badlogic.ashley.core.Component;

public class SpawnComponent extends Component {

    private Position spawnPosition;
    private Area area;
    private float cooldown;

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public void refreshCooldown() {
        cooldown = Rnd.between(10000, 40000);
    }
}
