package com.acme.gameserver.component;

import com.badlogic.ashley.core.Component;

public class DespawnComponent extends Component {

    private float cooldown;
    private boolean blinking;

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isBlinking() {
        return blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
    }
}
