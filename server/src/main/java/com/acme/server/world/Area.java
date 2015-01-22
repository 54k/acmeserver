package com.acme.server.world;

public class Area {

    private int x;
    private int y;
    private int width;
    private int height;

    public Area() {
    }

    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBottomLeftX() {
        return x;
    }

    public int getBottomLeftY() {
        return y;
    }

    public int getTopLeftX() {
        return getBottomLeftX() + height;
    }

    public int getTopLeftY() {
        return getBottomLeftY() + height;
    }

    public int getBottomRightX() {
        return getBottomLeftX() + width;
    }

    public int getBottomRightY() {
        return getBottomLeftY() + width;
    }

    public int getTopRightX() {
        return getBottomRightX() + height;
    }

    public int getTopRightY() {
        return getBottomRightY() + height;
    }
}
