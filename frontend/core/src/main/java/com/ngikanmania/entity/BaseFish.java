package com.ngikanmania.entity;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class BaseFish implements Poolable {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;
    public boolean active;
    public Texture texture;
    public IMovementStrategy movementStrategy;
    public FishType type;
    public Color color;
    
    public float width = 32f;
    public float height = 32f;
    public float rotation = 0f;
    
    // Extra values for movement patterns
    public float stateTime;
    public float startY;

    public BaseFish() {
        position = new Vector2();
        velocity = new Vector2();
        bounds = new Rectangle(0, 0, width, height);
        active = false;
        stateTime = 0f;
    }

    public void init(float x, float y, float vx, float vy, Texture texture, IMovementStrategy strategy, FishType type) {
        position.set(x, y);
        startY = y;
        velocity.set(vx, vy);
        this.movementStrategy = strategy;
        this.type = type;
        this.color = type.color;
        this.texture = texture;
        
        active = true;
        stateTime = 0f;
        
        if (movementStrategy != null) {
            movementStrategy.init(this);
        }
    }

    public boolean update(float delta) {
        stateTime += delta;
        boolean isAlive = true;
        if (movementStrategy != null) {
            isAlive = movementStrategy.update(this, delta);
        }
        bounds.setPosition(position.x - width / 2f, position.y - height / 2f);
        return isAlive;
    }

    public abstract void draw(SpriteBatch batch);

    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, 0);
        active = false;
        texture = null;
        movementStrategy = null;
        type = null;
        color = null;
        stateTime = 0f;
        startY = 0f;
    }
}

