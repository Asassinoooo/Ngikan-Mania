package com.ngikanmania;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Coin implements Poolable {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;
    
    private float width = 32f;
    private float height = 32f;
    private float stateTime = 0f;
    
    private float scatterDuration;
    private boolean isHoming;
    public int pointValue;
    
    public Coin() {
        position = new Vector2();
        velocity = new Vector2();
        bounds = new Rectangle(0, 0, width, height);
    }
    
    public void init(float startX, float startY, int points) {
        this.position.set(startX, startY);
        this.pointValue = points;
        
        // Random velocity for scatter effect
        float angle = MathUtils.random(0, MathUtils.PI2);
        float speed = MathUtils.random(150f, 400f);
        this.velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
        
        this.scatterDuration = MathUtils.random(1.0f, 3.0f);
        this.isHoming = false;
        this.stateTime = 0f;
        
        this.bounds.setPosition(position.x - width / 2f, position.y - height / 2f);
    }
    
    public boolean update(float delta, float targetX, float targetY, float worldWidth, float worldHeight) {
        stateTime += delta;
        
        if (!isHoming) {
            // Apply drag/friction so they slow down
            velocity.scl(0.95f);
            
            position.add(velocity.x * delta, velocity.y * delta);
            
            // Bounce off walls
            if (position.x < 0) { position.x = 0; velocity.x *= -1; }
            if (position.x > worldWidth) { position.x = worldWidth; velocity.x *= -1; }
            if (position.y < 0) { position.y = 0; velocity.y *= -1; }
            if (position.y > worldHeight) { position.y = worldHeight; velocity.y *= -1; }
            
            if (stateTime >= scatterDuration) {
                isHoming = true;
            }
        } else {
            // Homing towards the target (cannon)
            float dirX = targetX - position.x;
            float dirY = targetY - position.y;
            Vector2 dir = new Vector2(dirX, dirY).nor();
            
            // Accelerate rapidly towards cannon
            float homingSpeed = 800f; 
            position.add(dir.x * homingSpeed * delta, dir.y * homingSpeed * delta);
        }
        
        bounds.setPosition(position.x - width / 2f, position.y - height / 2f);
        
        // Return true as long as it's active. PlayScreen will handle deletion when it reaches cannon.
        return true; 
    }
    
    public void draw(SpriteBatch batch) {
        TextureRegion frame;
        if (!isHoming) {
            // Spinning animation
            frame = Assets.getInstance().coinAnimation.getKeyFrame(stateTime);
        } else {
            // Keyframe facing camera — use getKeyFrame(0f) to avoid raw Object[] cast
            frame = Assets.getInstance().coinAnimation.getKeyFrame(0f);
        }
        
        batch.draw(frame, position.x - width / 2f, position.y - height / 2f, width, height);
    }
    
    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, 0);
        stateTime = 0f;
        isHoming = false;
        pointValue = 0;
    }
}
