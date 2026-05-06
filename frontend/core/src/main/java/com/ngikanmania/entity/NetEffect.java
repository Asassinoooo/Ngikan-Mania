package com.ngikanmania.entity;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NetEffect implements Poolable {
    public Vector2 position;
    private float stateTime = 0f;
    private float duration = 0.3f; // 0.3 seconds animation
    public float radius;

    public NetEffect() {
        position = new Vector2();
    }

    public void init(float x, float y, float radius) {
        this.position.set(x, y);
        this.radius = radius;
        this.stateTime = 0f;
    }

    public boolean update(float delta) {
        stateTime += delta;
        return stateTime < duration;
    }

    public void draw(SpriteBatch batch) {
        float width = radius * 2f;
        float height = radius * 2f;
        // Fade out alpha effect based on stateTime
        float alpha = 1.0f - (stateTime / duration);
        if (alpha < 0f) alpha = 0f;
        
        batch.setColor(1, 1, 1, alpha);
        batch.draw(Assets.getInstance().simpleNetTexture, position.x - width / 2f, position.y - height / 2f, width, height);
        batch.setColor(1, 1, 1, 1); // Reset color
    }

    @Override
    public void reset() {
        position.set(0, 0);
        stateTime = 0f;
        radius = 0f;
    }
}

