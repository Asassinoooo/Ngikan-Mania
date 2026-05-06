package com.ngikanmania.entity;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

import com.badlogic.gdx.graphics.Color;

public enum FishType {
    SMALL(40f, 0.1f, 5, 10, Color.GREEN, 10),
    MEDIUM(80f, 0.02f, 2, 3, Color.YELLOW, 50),
    LARGE(160f, 0.005f, 1, 1, Color.RED, 200);

    public final float size;
    public final float deathProbability;
    public final int swarmMin;
    public final int swarmMax;
    public final Color color;
    public final int points;

    FishType(float size, float deathProbability, int swarmMin, int swarmMax, Color color, int points) {
        this.size = size;
        this.deathProbability = deathProbability;
        this.swarmMin = swarmMin;
        this.swarmMax = swarmMax;
        this.color = color;
        this.points = points;
    }
}

