package com.acme.gameserver.entity;

import com.acme.core.ashley.Archetype;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {

    // Player
    WARRIOR(1, Archetypes.PLAYER_TYPE),

    // Mobs
    RAT(2, Archetypes.CREATURE_TYPE),
    SKELETON(3, Archetypes.CREATURE_TYPE),
    GOBLIN(4, Archetypes.CREATURE_TYPE),
    OGRE(5, Archetypes.CREATURE_TYPE),
    SPECTRE(6, Archetypes.CREATURE_TYPE),
    CRAB(7, Archetypes.CREATURE_TYPE),
    BAT(8, Archetypes.CREATURE_TYPE),
    WIZARD(9, Archetypes.CREATURE_TYPE),
    EYE(10, Archetypes.CREATURE_TYPE),
    SNAKE(11, Archetypes.CREATURE_TYPE),
    SKELETON2(12, Archetypes.CREATURE_TYPE),
    BOSS(13, Archetypes.CREATURE_TYPE),
    DEATHKNIGHT(14, Archetypes.CREATURE_TYPE),

    // Armors
    FIREFOX(20, Archetypes.ITEM_TYPE),
    CLOTHARMOR(21, Archetypes.ITEM_TYPE),
    LEATHERARMOR(22, Archetypes.ITEM_TYPE),
    MAILARMOR(23, Archetypes.ITEM_TYPE),
    PLATEARMOR(24, Archetypes.ITEM_TYPE),
    REDARMOR(25, Archetypes.ITEM_TYPE),
    GOLDENARMOR(26, Archetypes.ITEM_TYPE),

    // Objects
    FLASK(35, Archetypes.ITEM_TYPE),
    BURGER(36, Archetypes.ITEM_TYPE),
    CHEST(37, Archetypes.CHEST_TYPE),
    FIREPOTION(38, Archetypes.ITEM_TYPE),
    CAKE(39, Archetypes.ITEM_TYPE),

    // NPCs
    GUARD(40, Archetypes.BASE_TYPE),
    KING(41, Archetypes.BASE_TYPE),
    OCTOCAT(42, Archetypes.BASE_TYPE),
    VILLAGEGIRL(43, Archetypes.BASE_TYPE),
    VILLAGER(44, Archetypes.BASE_TYPE),
    PRIEST(45, Archetypes.BASE_TYPE),
    SCIENTIST(46, Archetypes.BASE_TYPE),
    AGENT(47, Archetypes.BASE_TYPE),
    RICK(48, Archetypes.BASE_TYPE),
    NYAN(49, Archetypes.BASE_TYPE),
    SORCERER(50, Archetypes.BASE_TYPE),
    BEACHNPC(51, Archetypes.BASE_TYPE),
    FORESTNPC(52, Archetypes.BASE_TYPE),
    DESERTNPC(53, Archetypes.BASE_TYPE),
    LAVANPC(54, Archetypes.BASE_TYPE),
    CODER(55, Archetypes.BASE_TYPE),

    // Weapons
    SWORD1(60, Archetypes.ITEM_TYPE),
    SWORD2(61, Archetypes.ITEM_TYPE),
    REDSWORD(62, Archetypes.ITEM_TYPE),
    GOLDENSWORD(63, Archetypes.ITEM_TYPE),
    MORNINGSTAR(64, Archetypes.ITEM_TYPE),
    AXE(65, Archetypes.ITEM_TYPE),
    BLUESWORD(66, Archetypes.ITEM_TYPE);

    private final int id;
    private final Archetype archetype;

    Type(int id, Archetype archetype) {
        this.id = id;
        this.archetype = archetype;
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

    public Archetype getArchetype() {
        return archetype;
    }
}
