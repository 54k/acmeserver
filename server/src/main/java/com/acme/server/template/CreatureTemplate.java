package com.acme.server.template;

import com.acme.server.entities.Type;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatureTemplate {

    @JsonProperty("hp")
    private int hitPoints;
    private int weapon;
    private int armor;
    private Map<Type, Integer> drops;

    public int getHitPoints() {
        return hitPoints;
    }

    public int getWeapon() {
        return weapon;
    }

    public int getArmor() {
        return armor;
    }

    public Map<Type, Integer> getDrops() {
        return drops;
    }
}
