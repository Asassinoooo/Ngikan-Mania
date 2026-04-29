package com.ngikanmania;

import com.badlogic.gdx.graphics.Color;

public class CatchEvent {
    public final int points;
    public final String fishTypeName;
    public final Color fishColor;
    public final float x;
    public final float y;

    public CatchEvent(int points, String fishTypeName, Color fishColor, float x, float y) {
        this.points = points;
        this.fishTypeName = fishTypeName;
        this.fishColor = fishColor;
        this.x = x;
        this.y = y;
    }
}
