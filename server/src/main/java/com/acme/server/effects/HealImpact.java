package com.acme.server.effects;

import com.acme.engine.effects.Impact;

public final class HealImpact extends Impact {

    private int amount;

    public HealImpact() {
    }

    public HealImpact(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
