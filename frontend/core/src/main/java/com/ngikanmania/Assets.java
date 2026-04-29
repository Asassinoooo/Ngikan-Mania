package com.ngikanmania;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Singleton implementation for managing game assets like SpriteBatch and textures.
 */
public class Assets {
    private static Assets instance;
    public SpriteBatch batch;
    public Texture placeholderTexture;
    public com.badlogic.gdx.graphics.g2d.BitmapFont font;
    
    public Animation<TextureRegion> clownfishAnimation;
    private final com.badlogic.gdx.utils.Array<Texture> textureDisposeList;

    private Assets() {
        batch = new SpriteBatch();
        font = new com.badlogic.gdx.graphics.g2d.BitmapFont(); // Default font
        textureDisposeList = new com.badlogic.gdx.utils.Array<>();
        
        // Generate a simple white rectangular placeholder for the cannon/player
        Pixmap pixmap = new Pixmap(32, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        placeholderTexture = new Texture(pixmap);
        pixmap.dispose();

        // Load Clownfish Animation (0.1f duration, LOOP)
        Texture f1 = new Texture("clownfish/clownfish1.png");
        Texture f2 = new Texture("clownfish/clownfish2.png");
        Texture f3 = new Texture("clownfish/clownfish3.png");
        textureDisposeList.add(f1);
        textureDisposeList.add(f2);
        textureDisposeList.add(f3);

        com.badlogic.gdx.utils.Array<TextureRegion> frames = new com.badlogic.gdx.utils.Array<>();
        frames.add(new TextureRegion(f1));
        frames.add(new TextureRegion(f2));
        frames.add(new TextureRegion(f3));
        
        clownfishAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    public void dispose() {
        batch.dispose();
        placeholderTexture.dispose();
        if (font != null) font.dispose();
        for (Texture t : textureDisposeList) {
            t.dispose();
        }
    }
}
