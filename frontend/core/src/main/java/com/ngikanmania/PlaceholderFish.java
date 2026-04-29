package com.ngikanmania;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlaceholderFish extends BaseFish {
    @Override
    public void init(float x, float y, float vx, float vy, Texture texture, IMovementStrategy strategy, FishType type) {
        super.init(x, y, vx, vy, texture, strategy, type);
        this.width = type.size;
        this.height = type.size;
        
        bounds.setSize(width, height);
        bounds.setPosition(x - width / 2f, y - height / 2f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (texture != null) {
            // Render the original colored placeholder logic for other fish types
            batch.setColor(color);
            batch.draw(texture, 
                position.x - width / 2f, position.y - height / 2f, 
                width / 2f, height / 2f, 
                width, height, 
                1f, 1f, 
                rotation, 
                0, 0, texture.getWidth(), texture.getHeight(), 
                false, false);
            batch.setColor(Color.WHITE); // Reset color
        }
    }
}