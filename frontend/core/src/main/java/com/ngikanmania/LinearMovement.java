package com.ngikanmania;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

/**
 * Strategy Pattern: Linear movement
 */
public class LinearMovement implements IMovementStrategy {
    private final float WORLD_WIDTH = 1280f;
    private final float WORLD_HEIGHT = 720f;

    @Override
    public void init(BaseFish entity) {
        // If velocity is already provided (e.g., for swarm followers), just set rotation and skip picking a new target
        if (entity.velocity.x != 0 || entity.velocity.y != 0) {
            entity.rotation = entity.velocity.angleDeg() - 90f;
            return;
        }

        float startX = entity.position.x;
        float startY = entity.position.y;
        
        // Find which edge it spawned on based on coords
        int spawnEdge = -1;
        if (startY > WORLD_HEIGHT) spawnEdge = 0; // Top
        else if (startY < 0) spawnEdge = 1; // Bottom
        else if (startX < 0) spawnEdge = 2; // Left
        else spawnEdge = 3; // Right

        float targetX = 0, targetY = 0;
        boolean validTarget = false;
        
        while (!validTarget) {
            int targetEdge = MathUtils.random(0, 3);
            if (targetEdge == spawnEdge) continue; // Must be one of the other three edges
            
            switch (targetEdge) {
                case 0: // Top
                    targetX = MathUtils.random(0f, WORLD_WIDTH);
                    targetY = 870f;
                    break;
                case 1: // Bottom
                    targetX = MathUtils.random(0f, WORLD_WIDTH);
                    targetY = -150f;
                    break;
                case 2: // Left
                    targetX = -150f;
                    targetY = MathUtils.random(0f, WORLD_HEIGHT);
                    break;
                case 3: // Right
                    targetX = 1430f;
                    targetY = MathUtils.random(0f, WORLD_HEIGHT);
                    break;
            }
            
            // Distance check >= 720 (Vertical height of our frame)
            float dist = Vector2.dst(startX, startY, targetX, targetY);
            if (dist >= 720f) {
                validTarget = true;
            }
        }

        // Linear Strategy vector
        Vector2 direction = new Vector2(targetX - startX, targetY - startY).nor();
        float fishSpeed = MathUtils.random(50f, 150f);
        entity.velocity.set(direction.x * fishSpeed, direction.y * fishSpeed);
        
        // Visuals: Rotate the fish sprite
        entity.rotation = entity.velocity.angleDeg() - 90f;
    }

    @Override
    public boolean update(BaseFish entity, float delta) {
        entity.position.add(entity.velocity.x * delta, entity.velocity.y * delta);
        
        // Despawn logic: Check if 200 units outside the 1280x720 frame
        // (x < -200 || x > 1480 || y < -200 || y > 920)
        if (entity.position.x < -200f || entity.position.x > 1480f || 
            entity.position.y < -200f || entity.position.y > 920f) {
            return false; 
        }
        
        return true; 
    }
}
