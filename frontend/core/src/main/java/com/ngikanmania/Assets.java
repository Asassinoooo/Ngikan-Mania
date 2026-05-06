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
    public Texture cannonTexture;
    public Texture bulletTexture;
    public com.badlogic.gdx.graphics.g2d.BitmapFont font;
    
    public Animation<TextureRegion> clownfishAnimation;
    public Animation<TextureRegion> lionfishAnimation;
    public Animation<TextureRegion> mantaAnimation;
    public Animation<TextureRegion> coinAnimation;
    public Texture backgroundTexture;
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

        cannonTexture = new Texture("cannon/Cannon.png");
        textureDisposeList.add(cannonTexture);

        bulletTexture = new Texture("bullet/Bullet.png");
        textureDisposeList.add(bulletTexture);

        backgroundTexture = new Texture("background.png");
        textureDisposeList.add(backgroundTexture);

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

        // Load Lionfish Animation (0.1f duration, LOOP)
        Texture l1 = new Texture("lionfish/lionfish1.png");
        Texture l2 = new Texture("lionfish/lionfish2.png");
        Texture l3 = new Texture("lionfish/lionfish3.png");
        textureDisposeList.add(l1);
        textureDisposeList.add(l2);
        textureDisposeList.add(l3);

        com.badlogic.gdx.utils.Array<TextureRegion> lionfishFrames = new com.badlogic.gdx.utils.Array<>();
        lionfishFrames.add(new TextureRegion(l1));
        lionfishFrames.add(new TextureRegion(l2));
        lionfishFrames.add(new TextureRegion(l3));
        
        lionfishAnimation = new Animation<>(0.1f, lionfishFrames, Animation.PlayMode.LOOP);

        // Load Manta Ray Animation (0.1f duration, LOOP)
        Texture m1 = new Texture("manta/manta1.png");
        Texture m2 = new Texture("manta/manta2.png");
        Texture m3 = new Texture("manta/manta3.png");
        textureDisposeList.add(m1);
        textureDisposeList.add(m2);
        textureDisposeList.add(m3);

        com.badlogic.gdx.utils.Array<TextureRegion> mantaFrames = new com.badlogic.gdx.utils.Array<>();
        mantaFrames.add(new TextureRegion(m1));
        mantaFrames.add(new TextureRegion(m2));
        mantaFrames.add(new TextureRegion(m3));
        
        mantaAnimation = new Animation<>(0.1f, mantaFrames, Animation.PlayMode.LOOP);

        // Load Coin Animation
        Texture c1 = new Texture("coin/coin1.png");
        Texture c2 = new Texture("coin/coin2.png");
        Texture c3 = new Texture("coin/coin3.png");
        textureDisposeList.add(c1);
        textureDisposeList.add(c2);
        textureDisposeList.add(c3);

        com.badlogic.gdx.utils.Array<TextureRegion> coinFrames = new com.badlogic.gdx.utils.Array<>();
        coinFrames.add(new TextureRegion(c1));
        coinFrames.add(new TextureRegion(c2));
        coinFrames.add(new TextureRegion(c3));
        
        coinAnimation = new Animation<>(0.1f, coinFrames, Animation.PlayMode.LOOP);
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
