package com.acme.server.component;

import com.badlogic.ashley.core.Component;

public class InventoryComponent extends Component {

    private int armor;
    private int weapon;

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
