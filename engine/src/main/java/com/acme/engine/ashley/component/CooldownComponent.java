package com.acme.engine.ashley.component;

import com.badlogic.ashley.core.Component;

public class CooldownComponent extends Component {

    private float cooldown;
    private float initialCooldown;

    public CooldownComponent() {
    }

    public CooldownComponent(float cooldown, float initialCooldown) {
        this.cooldown = cooldown;
        this.initialCooldown = initialCooldown;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public float getInitialCooldown() {
        return initialCooldown;
    }

    public void setInitialCooldown(float initialCooldown) {
        this.initialCooldown = initialCooldown;
    }

    public void decreaseCooldown(float deltaTime) {
        setCooldown(Math.max(0, cooldown - deltaTime));
    }

    public void refreshCooldown() {
        setCooldown(initialCooldown);
    }

    public boolean isReady() {
        return cooldown <= 0;
    }
}
