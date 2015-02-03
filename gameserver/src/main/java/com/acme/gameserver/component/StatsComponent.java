package com.acme.gameserver.component;

import com.badlogic.ashley.core.Component;

public class StatsComponent extends Component {

    private int hitPoints;
    private int maxHitPoints;

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }
}
