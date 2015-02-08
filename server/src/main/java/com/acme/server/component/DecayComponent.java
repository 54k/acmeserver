package com.acme.server.component;

import com.acme.engine.ashley.component.CooldownComponent;

public class DecayComponent extends CooldownComponent {

    private boolean blinking;

    public boolean isBlinking() {
        return blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
    }
}
