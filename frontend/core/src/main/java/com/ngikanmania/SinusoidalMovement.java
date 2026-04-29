package com.ngikanmania;

/**
 * Strategy Pattern: Sinusoidal movement
 */
public class SinusoidalMovement implements IMovementStrategy {
    private final float amplitude = 50f;
    private final float frequency = 2f;

    @Override
    public void init(BaseFish entity) {
        // Simple default angle for wavy
        entity.rotation = entity.velocity.angleDeg() - 90f;
    }

    @Override
    public boolean update(BaseFish entity, float delta) {
        entity.position.x += entity.velocity.x * delta;
        // Wavy movement along Y axis
        entity.position.y = entity.startY + amplitude * (float)Math.sin(entity.stateTime * frequency);
        
        // Despawn logic matching the old PlayScreen
        return !(entity.position.x < -200f || entity.position.x > 1480f || 
                 entity.position.y < -200f || entity.position.y > 920f);
    }
}
