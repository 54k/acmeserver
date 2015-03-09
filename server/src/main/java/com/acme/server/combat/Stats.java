package com.acme.server.combat;

import com.acme.ecs.core.Component;

public final class Stats extends Component {

    int hitPoints;
    int maxHitPoints;

    void resetHitPoints() {
        hitPoints = maxHitPoints;
    }
}
