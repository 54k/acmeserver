package com.acme.server.impacts;

import com.acme.engine.mechanics.impact.Impact;

public final class HealImpact extends Impact {

    private int amount;

    public HealImpact(int amount) {
        this(amount, 1, 0);
    }

    public HealImpact(int amount, int ticks, int interval) {
        super(ticks, interval);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
