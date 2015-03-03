package com.acme.server.entities;

import com.acme.engine.ecs.entities.EntityBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {

    // Player
    WARRIOR(1, EntityBuilders.PLAYER_TYPE),

    // Mobs
    RAT(2, EntityBuilders.CREATURE_TYPE),
    SKELETON(3, EntityBuilders.CREATURE_TYPE),
    GOBLIN(4, EntityBuilders.CREATURE_TYPE),
    OGRE(5, EntityBuilders.CREATURE_TYPE),
    SPECTRE(6, EntityBuilders.CREATURE_TYPE),
    CRAB(7, EntityBuilders.CREATURE_TYPE),
    BAT(8, EntityBuilders.CREATURE_TYPE),
    WIZARD(9, EntityBuilders.CREATURE_TYPE),
    EYE(10, EntityBuilders.CREATURE_TYPE),
    SNAKE(11, EntityBuilders.CREATURE_TYPE),
    SKELETON2(12, EntityBuilders.CREATURE_TYPE),
    BOSS(13, EntityBuilders.CREATURE_TYPE),
    DEATHKNIGHT(14, EntityBuilders.CREATURE_TYPE),

    // Armors
    FIREFOX(20, EntityBuilders.ITEM_TYPE),
    CLOTHARMOR(21, EntityBuilders.ITEM_TYPE),
    LEATHERARMOR(22, EntityBuilders.ITEM_TYPE),
    MAILARMOR(23, EntityBuilders.ITEM_TYPE),
    PLATEARMOR(24, EntityBuilders.ITEM_TYPE),
    REDARMOR(25, EntityBuilders.ITEM_TYPE),
    GOLDENARMOR(26, EntityBuilders.ITEM_TYPE),

    // Objects
    FLASK(35, EntityBuilders.ITEM_TYPE),
    BURGER(36, EntityBuilders.ITEM_TYPE),
    CHEST(37, EntityBuilders.CHEST_TYPE),
    FIREPOTION(38, EntityBuilders.ITEM_TYPE),
    CAKE(39, EntityBuilders.ITEM_TYPE),

    // NPCs
    GUARD(40, EntityBuilders.BASE_TYPE),
    KING(41, EntityBuilders.BASE_TYPE),
    OCTOCAT(42, EntityBuilders.BASE_TYPE),
    VILLAGEGIRL(43, EntityBuilders.BASE_TYPE),
    VILLAGER(44, EntityBuilders.BASE_TYPE),
    PRIEST(45, EntityBuilders.BASE_TYPE),
    SCIENTIST(46, EntityBuilders.BASE_TYPE),
    AGENT(47, EntityBuilders.BASE_TYPE),
    RICK(48, EntityBuilders.BASE_TYPE),
    NYAN(49, EntityBuilders.BASE_TYPE),
    SORCERER(50, EntityBuilders.BASE_TYPE),
    BEACHNPC(51, EntityBuilders.BASE_TYPE),
    FORESTNPC(52, EntityBuilders.BASE_TYPE),
    DESERTNPC(53, EntityBuilders.BASE_TYPE),
    LAVANPC(54, EntityBuilders.BASE_TYPE),
    CODER(55, EntityBuilders.BASE_TYPE),

    // Weapons
    SWORD1(60, EntityBuilders.ITEM_TYPE),
    SWORD2(61, EntityBuilders.ITEM_TYPE),
    REDSWORD(62, EntityBuilders.ITEM_TYPE),
    GOLDENSWORD(63, EntityBuilders.ITEM_TYPE),
    MORNINGSTAR(64, EntityBuilders.ITEM_TYPE),
    AXE(65, EntityBuilders.ITEM_TYPE),
    BLUESWORD(66, EntityBuilders.ITEM_TYPE);

    private final int id;
    private final EntityBuilder entityBuilder;

    Type(int id, EntityBuilder entityBuilder) {
        this.id = id;
        this.entityBuilder = entityBuilder;
    }

    @JsonCreator
    public static Type fromString(String name) {
        for (Type type : Type.values()) {
            if (name.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return null;
    }

    public static Type fromId(int id) {
        for (Type type : Type.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }

    public EntityBuilder getEntityBuilder() {
        return entityBuilder;
    }
}
