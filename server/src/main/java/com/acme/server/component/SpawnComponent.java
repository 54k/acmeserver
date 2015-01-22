package com.acme.server.component;

import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.badlogic.ashley.core.Component;

public class SpawnComponent extends Component {

    private Area area;
    private float cooldown;

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
        int minRespawnDelay = 10000;
        int maxRespawnDelay = 40000;
        cooldown = Rnd.between(minRespawnDelay, maxRespawnDelay);
    }
}
