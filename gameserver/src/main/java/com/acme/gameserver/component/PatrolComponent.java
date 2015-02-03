package com.acme.gameserver.component;

import com.badlogic.ashley.core.Component;

public class PatrolComponent extends Component {

    private float cooldown;

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }
}
