package com.acme.gameserver.world;

public enum Orientation {

    RIGHT(1),
    BOTTOM(2),
    LEFT(3),
    TOP(4);

    private int value;

    Orientation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
