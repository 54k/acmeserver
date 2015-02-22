package com.acme.server.component;

import com.acme.engine.aegis.core.Component;

public class PatrolComponent extends Component {

    private float cooldown;

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }
}
