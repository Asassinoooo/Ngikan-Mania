package com.ngikanmania;

/**
 * Strategy Pattern: Defines the movement and collision behavior of a bullet.
 */
public interface BulletBehavior {
    /**
     * Updates the bullet's state.
     * @param bullet The bullet to update.
     * @param delta Time since last frame.
     * @param worldWidth The virtual width of the game world.
     * @param worldHeight The virtual height of the game world.
     * @return true if the bullet is still active, false if it should be pooled/removed.
     */
    boolean update(Bullet bullet, float delta, float worldWidth, float worldHeight);
}
