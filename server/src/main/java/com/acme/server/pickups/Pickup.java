package com.acme.server.pickups;

import com.badlogic.ashley.core.Component;

public class Pickup extends Component {

    private PickupType pickupType;
    private int amount;

    public PickupType getPickupType() {
        return pickupType;
    }

    public void setPickupType(PickupType pickupType) {
        this.pickupType = pickupType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static enum PickupType {
        WEAPON, ARMOR, HEALTH_POTION, FIREFOX_POTION
    }
}
