package com.ngikanmania;

import com.badlogic.gdx.math.MathUtils;

/**
 * Strategy Pattern: Implements bouncing physics for bullets against the top, left, and right borders.
 */
public class BouncingBehavior implements BulletBehavior {
    
    @Override
    public boolean update(Bullet bullet, float delta, float worldWidth, float worldHeight) {
        bullet.position.add(bullet.velocity.x * delta, bullet.velocity.y * delta);

        boolean bounced = false;

        // Bounce left border
        if (bullet.position.x < 0) {
            bullet.position.x = 0;
            bullet.velocity.x = Math.abs(bullet.velocity.x);
            bounced = true;
        } 
        // Bounce right border
        else if (bullet.position.x > worldWidth) {
            bullet.position.x = worldWidth;
            bullet.velocity.x = -Math.abs(bullet.velocity.x);
            bounced = true;
        }

        // Bounce top border
        if (bullet.position.y > worldHeight) {
            bullet.position.y = worldHeight;
            bullet.velocity.y = -Math.abs(bullet.velocity.y);
            bounced = true;
        }
        // Bounce bottom border
        else if (bullet.position.y < 0) {
            bullet.position.y = 0;
            bullet.velocity.y = Math.abs(bullet.velocity.y);
            bounced = true;
        }

        // Update visual rotation if direction changed
        if (bounced) {
            bullet.rotation = MathUtils.atan2(bullet.velocity.y, bullet.velocity.x) * MathUtils.radiansToDegrees - 90f;
        }

        return true; // Keep active
    }
}
