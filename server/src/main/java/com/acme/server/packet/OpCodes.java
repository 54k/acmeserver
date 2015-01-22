package com.acme.server.packet;

public final class OpCodes {
    private OpCodes() {
    }

    public static final int HELLO = 0;
    public static final int WELCOME = 1;
    public static final int SPAWN = 2;
    public static final int DESPAWN = 3;
    public static final int MOVE = 4;
    public static final int LOOTMOVE = 5;
    public static final int AGGRO = 6;
    public static final int ATTACK = 7;
    public static final int HIT = 8;
    public static final int HURT = 9;
    public static final int HEALTH = 10;
    public static final int CHAT = 11;
    public static final int LOOT = 12;
    public static final int EQUIP = 13;
    public static final int DROP = 14;
    public static final int TELEPORT = 15;
    public static final int DAMAGE = 16;
    public static final int POPULATION = 17;
    public static final int KILL = 18;
    public static final int LIST = 19;
    public static final int WHO = 20;
    public static final int ZONE = 21;
    public static final int DESTROY = 22;
    public static final int HP = 23;
    public static final int BLINK = 24;
    public static final int OPEN = 25;
    public static final int CHECK = 26;
}
