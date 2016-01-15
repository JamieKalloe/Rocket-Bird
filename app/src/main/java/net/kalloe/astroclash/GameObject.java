package net.kalloe.astroclash;

import android.graphics.Rect;

/**
 * Created by Jamie on 15-1-2016.
 */
public abstract class GameObject {

    protected int x, y, dy, dx, width, height;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDy() {
        return dy;
    }

    public int getDx() {
        return dx;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rect getRectangle() {
        return new Rect(x, y, x+width, y+height);
    }
}
