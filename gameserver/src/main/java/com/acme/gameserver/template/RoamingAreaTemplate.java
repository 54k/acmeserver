package com.acme.gameserver.template;

import com.acme.gameserver.entity.Type;

public class RoamingAreaTemplate {

    private int id;
    private int x;
    private int y;
    private int width;
    private int height;
    private Type type;
    private int nb;

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Type getType() {
        return type;
    }

    public int getNb() {
        return nb;
    }
}