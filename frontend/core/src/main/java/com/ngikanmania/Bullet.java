package com.ngikanmania;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Object Pool Pattern: Bullet implements Poolable so instances can be reused.
 */
public class Bullet implements Poolable {
    public Vector2 position;
    public Vector2 velocity;
    public boolean active;
    public float rotation;
    public float timeAlive;
    public BulletBehavior behavior; // Strategy Pattern: Current behavior
    
    private final float speed = 500f; // pixels per second
    private final float width = 8f;
    private final float height = 16f;
    public com.badlogic.gdx.math.Rectangle bounds; // For collision detection

    public Bullet() {
        position = new Vector2();
        velocity = new Vector2();
        bounds = new com.badlogic.gdx.math.Rectangle(0, 0, width, height);
        active = false;
    }

    /**
     * Initializes the bullet's starting position, direction, and behavior.
     * @param x starting x
     * @param y starting y
     * @param dirX normalized direction x
     * @param dirY normalized direction y
     * @param angle rotation angle in degrees for rendering
     * @param behavior strategy to resolve movement and collision
     */
    public void init(float x, float y, float dirX, float dirY, float angle, BulletBehavior behavior) {
        position.set(x, y);
        bounds.setPosition(x - width / 2f, y - height / 2f);
        velocity.set(dirX * speed, dirY * speed);
        this.rotation = angle;
        this.timeAlive = 0f;
        this.behavior = behavior;
        active = true;
    }

    public boolean update(float delta, float worldWidth, float worldHeight) {
        timeAlive += delta;
        if (timeAlive >= 30f) {
            return false; // Time limit reached, free back to pool
        }

        boolean activeResult = true;
        if (behavior != null) {
            // Apply the chosen strategy logic
            activeResult = behavior.update(this, delta, worldWidth, worldHeight);
        } else {
            // Default free-movement strategy (despawns out of bounds)
            position.add(velocity.x * delta, velocity.y * delta);
            activeResult = !(position.x < 0 || position.x > worldWidth || position.y < 0 || position.y > worldHeight);
        }

        // Update bounds to match new position
        bounds.setPosition(position.x - width / 2f, position.y - height / 2f);
        
        return activeResult;
    }

    public void draw(SpriteBatch batch, Texture tex) {
        batch.draw(tex, 
            position.x - width / 2f, position.y - height / 2f, 
            width / 2f, height / 2f, 
            width, height, 
            1f, 1f, 
            rotation, 
            0, 0, tex.getWidth(), tex.getHeight(), 
            false, false);
    }

    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, 0);
        active = false;
        rotation = 0f;
        timeAlive = 0f;
        behavior = null;
    }
}
