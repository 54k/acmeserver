package com.acme.server.inventory;

import com.acme.ecs.core.Component;

public final class Inventory extends Component {

    int armor;
    int weapon;

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }
}
