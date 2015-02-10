package com.acme.server.component;

import com.acme.engine.ashley.component.TimerComponent;

public class DecayComponent extends TimerComponent {

    private boolean blinking;

    public boolean isBlinking() {
        return blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
    }
}
