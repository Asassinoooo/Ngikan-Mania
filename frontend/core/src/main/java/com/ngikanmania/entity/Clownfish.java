package com.ngikanmania.entity;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Clownfish extends BaseFish {
    @Override
    public void init(float x, float y, float vx, float vy, Texture texture, IMovementStrategy strategy, FishType type) {
        super.init(x, y, vx, vy, null, strategy, type);
        
        // Calculate aspect ratio from animation frame
        TextureRegion currentFrame = Assets.getInstance().clownfishAnimation.getKeyFrame(0f);
        float aspectRatio = (float) currentFrame.getRegionWidth() / (float) currentFrame.getRegionHeight();
        
        this.width = type.size;
        this.height = type.size / aspectRatio;
        
        bounds.setSize(width, height);
        bounds.setPosition(x - width / 2f, y - height / 2f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = Assets.getInstance().clownfishAnimation.getKeyFrame(stateTime);

        // When the fish turns to the left, the rotation makes it upside-down.
        // Flipping it along the local Y-axis corrects its orientation so the belly stays down.
        boolean flipY = velocity.x < 0; 
        
        // Render clownsfish with a +90 degree offset rotation
        batch.draw(currentFrame.getTexture(), 
            position.x - width / 2f, position.y - height / 2f, 
            width / 2f, height / 2f, 
            width, height, 
            1f, 1f, 
            rotation + 90f, 
            currentFrame.getRegionX(), currentFrame.getRegionY(), 
            currentFrame.getRegionWidth(), currentFrame.getRegionHeight(), 
            false, flipY);
    }
}
