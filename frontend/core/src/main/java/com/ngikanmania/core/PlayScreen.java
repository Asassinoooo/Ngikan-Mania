package com.ngikanmania.core;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * PlayScreen implementing the State pattern.
 * Represents the active gameplay state where entities are rendered and updated.
 */
public class PlayScreen implements Screen, FishObserver {
    private final Assets assets;
    private final Texture cannonTex;
    private final Texture bulletTex;
    private final float playerWidth = 102.4f;  // 128 * 0.8 scale
    private final float playerHeight = 102.4f; // 128 * 0.8 scale
    private final float cannonScaleX = 0.646f; // Non-1:1 scale from draw() constant
    private final Vector2 playerPos;
    
    // Viewport & Resolution configurations
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float WORLD_WIDTH = 1280f;
    private final float WORLD_HEIGHT = 720f;
    private final ShapeRenderer shapeRenderer;
    private final boolean showHitboxes = true;
    
    // Strategy instance (Singleton pattern can apply here to save memory)
    private final BulletBehavior bouncingStrategy;

    // Object Pool Pattern: Pool for Bullet instances
    private final com.badlogic.gdx.utils.Pool<Bullet> bulletPool;
    private final com.badlogic.gdx.utils.Array<Bullet> activeBullets;

    // Object Pool Pattern: Pool for Fish instances
    private final com.badlogic.gdx.utils.Pool<Clownfish> clownfishPool;
    private final com.badlogic.gdx.utils.Pool<Lionfish> lionfishPool;
    private final com.badlogic.gdx.utils.Pool<MantaRay> mantaRayPool;
    private final com.badlogic.gdx.utils.Array<BaseFish> activeFishes;
    private final com.badlogic.gdx.utils.Pool<Coin> coinPool;
    private final com.badlogic.gdx.utils.Array<Coin> activeCoins;
    private final com.badlogic.gdx.utils.Pool<NetEffect> netPool;
    private final com.badlogic.gdx.utils.Array<NetEffect> activeNets;
    private float fishSpawnTimer = 0f;

    // Movement Strategies for fishes
    private final IMovementStrategy linearMovement;
    private final IMovementStrategy sinusoidalMovement;

    // UI and Point System Observer
    private final Scoreboard scoreboard;

    public PlayScreen() {
        // Access Singleton instance
        assets = Assets.getInstance();
        cannonTex = assets.cannonTexture;
        bulletTex = assets.bulletTexture;
        playerPos = new Vector2();
        
        // Define Fixed Virtual Resolution
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        
        // Instantiate the UI Observer
        scoreboard = new Scoreboard();

        // Instantiate the bouncing physics strategy
        bouncingStrategy = new BouncingBehavior();

        // Initialize Object Pool for bullets
        bulletPool = new com.badlogic.gdx.utils.Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet();
            }
        };
        activeBullets = new com.badlogic.gdx.utils.Array<>();

        // Initialize Object Pool for fishes
        clownfishPool = new com.badlogic.gdx.utils.Pool<Clownfish>() {
            @Override
            protected Clownfish newObject() {
                return new Clownfish();
            }
        };
        
        lionfishPool = new com.badlogic.gdx.utils.Pool<Lionfish>() {
            @Override
            protected Lionfish newObject() {
                return new Lionfish();
            }
        };
        
        mantaRayPool = new com.badlogic.gdx.utils.Pool<MantaRay>() {
            @Override
            protected MantaRay newObject() {
                return new MantaRay();
            }
        };
        activeFishes = new com.badlogic.gdx.utils.Array<>();

        coinPool = new com.badlogic.gdx.utils.Pool<Coin>() {
            @Override
            protected Coin newObject() {
                return new Coin();
            }
        };
        activeCoins = new com.badlogic.gdx.utils.Array<>();

        netPool = new com.badlogic.gdx.utils.Pool<NetEffect>() {
            @Override
            protected NetEffect newObject() {
                return new NetEffect();
            }
        };
        activeNets = new com.badlogic.gdx.utils.Array<>();

        // Initialize Movement Strategies
        linearMovement = new LinearMovement();
        sinusoidalMovement = new SinusoidalMovement();
    }

    @Override
    public void show() {
        // Place the player at bottom center constrained to the virtual 1280x720 space
        playerPos.x = WORLD_WIDTH / 2f;
        playerPos.y = 50f;
    }

    @Override
    public void render(float delta) {
        // Set deep sea blue background
        ScreenUtils.clear(0f, 0.2f, 0.4f, 1f);
        
        // --- Spawner Logic ---
        fishSpawnTimer += delta;
        if (fishSpawnTimer >= 2f) {
            fishSpawnTimer = 0f;
            spawnFish();
        }
        
        // Apply viewport transformations to camera
        camera.update();
        assets.batch.setProjectionMatrix(camera.combined);

        // Track and project mouse position from Screen Pixels to 720p virtual units
        Vector3 mousePos = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        
        float diffX = mousePos.x - playerPos.x;
        float diffY = mousePos.y - playerPos.y;
        
        float angle = MathUtils.atan2(diffY, diffX) * MathUtils.radiansToDegrees;
        
        // Normalize angle for precise bounds clamping
        if (angle < -90) {
            angle += 360f; // Allow wrapping into the valid constraint space
        }
        
        // Cannon constraint logic: strictly from 0 to 180 degrees
        if (angle < 0f) angle = 0f;
        if (angle > 180f) angle = 180f;

        // Subtract 90 degrees since the default rectangular sprite orientation is "pointing up"
        float renderAngle = angle - 90f; 
        
        // Handle input - firing
        if (Gdx.input.justTouched()) {
            if (scoreboard.getPoints() > 0) { // Safety Check: Player has ammo/points
                // Because angle might be clamped, re-calculate the final firing direction vector
                float fireDirX = MathUtils.cosDeg(angle);
                float fireDirY = MathUtils.sinDeg(angle);
                    
                // Object Pool Pattern: Obtain a bullet
                Bullet bullet = bulletPool.obtain();
                bullet.init(playerPos.x, playerPos.y, fireDirX, fireDirY, renderAngle, bouncingStrategy);
                activeBullets.add(bullet);

                // Log Bullet Event & Deduct point
                scoreboard.onBulletFired(playerPos.x, playerPos.y, fireDirX, fireDirY);
            }
        }

        // Update active bullets using the Strategy Pattern
        for (int i = activeBullets.size - 1; i >= 0; i--) {
            Bullet b = activeBullets.get(i);
            
            // BulletBehavior strategy dictates if it should stay active
            boolean isAlive = b.update(delta, WORLD_WIDTH, WORLD_HEIGHT);
            
            if (!isAlive) {
                activeBullets.removeIndex(i);
                bulletPool.free(b); // Object Pool Pattern: Release Memory
                continue;
            }

            // Observer Pattern: Hit Detection
            boolean hit = false;
            for (int j = activeFishes.size - 1; j >= 0; j--) {
                BaseFish fish = activeFishes.get(j);
                if (b.bounds.overlaps(fish.bounds)) {
                    // Start collision calculation sequences (AoE)
                    float aoeRadius = 45f; // 90x90 total diameter (3x larger)
                    
                    // Visual Effect
                    NetEffect net = netPool.obtain();
                    net.init(b.position.x, b.position.y, aoeRadius);
                    activeNets.add(net);
                    
                    // AoE Hit detection (Rectangle overlap is much more reliable for large fishes)
                    com.badlogic.gdx.math.Rectangle aoeRect = new com.badlogic.gdx.math.Rectangle(
                        b.position.x - aoeRadius, 
                        b.position.y - aoeRadius, 
                        aoeRadius * 2f, 
                        aoeRadius * 2f
                    );
                    
                    for (int k = activeFishes.size - 1; k >= 0; k--) {
                        BaseFish target = activeFishes.get(k);
                        if (aoeRect.overlaps(target.bounds)) {
                            handleCollision(target);
                        }
                    }
                    
                    // Remove bullet and free it
                    activeBullets.removeIndex(i);
                    bulletPool.free(b);
                    hit = true;
                    break;
                }
            }
        }

        // Update active fishes
        for (int i = activeFishes.size - 1; i >= 0; i--) {
            BaseFish fish = activeFishes.get(i);
            boolean isAlive = fish.update(delta);

            if (!isAlive) {
                activeFishes.removeIndex(i);
                if (fish instanceof Clownfish) {
                    clownfishPool.free((Clownfish) fish);
                } else if (fish instanceof Lionfish) {
                    lionfishPool.free((Lionfish) fish);
                } else if (fish instanceof MantaRay) {
                    mantaRayPool.free((MantaRay) fish);
                }
            }
        }

        // Update active coins
        for (int i = activeCoins.size - 1; i >= 0; i--) {
            Coin coin = activeCoins.get(i);
            boolean isAlive = coin.update(delta, playerPos.x, playerPos.y, WORLD_WIDTH, WORLD_HEIGHT);
            
            // Check collision with cannon (player)
            com.badlogic.gdx.math.Rectangle playerBounds = new com.badlogic.gdx.math.Rectangle(playerPos.x - playerWidth / 2f, playerPos.y - playerHeight / 2f, playerWidth, playerHeight);
            if (coin.bounds.overlaps(playerBounds)) {
                scoreboard.onCoinCollected(coin.pointValue);
                activeCoins.removeIndex(i);
                coinPool.free(coin);
            } else if (!isAlive) {
                activeCoins.removeIndex(i);
                coinPool.free(coin);
            }
        }

        // Update active nets
        for (int i = activeNets.size - 1; i >= 0; i--) {
            NetEffect net = activeNets.get(i);
            boolean isAlive = net.update(delta);
            if (!isAlive) {
                activeNets.removeIndex(i);
                netPool.free(net);
            }
        }

        assets.batch.begin();
        
        // Render ocean background
        if (assets.backgroundTexture != null) {
            assets.batch.draw(assets.backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        
        // Render 720p Virtual Area Borders (Thickness: 4 pixels)
        float t = 4f;
        // Left
        assets.batch.draw(assets.placeholderTexture, 0, 0, t, WORLD_HEIGHT); 
        // Right
        assets.batch.draw(assets.placeholderTexture, WORLD_WIDTH - t, 0, t, WORLD_HEIGHT); 
        // Top
        assets.batch.draw(assets.placeholderTexture, 0, WORLD_HEIGHT - t, WORLD_WIDTH, t); 
        // Bottom
        assets.batch.draw(assets.placeholderTexture, 0, 0, WORLD_WIDTH, t); 
        
        // Render fishes
        for (BaseFish fish : activeFishes) {
            fish.draw(assets.batch);
        }

        // Render bullets
        for (Bullet b : activeBullets) {
            b.draw(assets.batch, bulletTex);
        }

        // Render nets over bullets and fishes
        for (NetEffect net : activeNets) {
            net.draw(assets.batch);
        }
        
        // Render the cannon sprite rotating around its center to face the cursor
        // The image was flipped 180 degrees, so we add 90f instead of subtracting 90f
        assets.batch.draw(cannonTex, 
            playerPos.x - playerWidth / 2f, playerPos.y - playerHeight / 2f, 
            playerWidth / 2f, playerHeight / 2f, 
            playerWidth, playerHeight, 
            cannonScaleX, 1f, 
            renderAngle + 0f, 
            0, 0, cannonTex.getWidth(), cannonTex.getHeight(), 
            false, false);

        // Render coins on top of everything including cannon
        for (Coin coin : activeCoins) {
            coin.draw(assets.batch);
        }
            
        assets.batch.end();

        if (showHitboxes) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            for (Bullet b : activeBullets) {
                shapeRenderer.rect(b.bounds.x, b.bounds.y, b.bounds.width, b.bounds.height);
            }

            for (BaseFish fish : activeFishes) {
                shapeRenderer.rect(fish.bounds.x, fish.bounds.y, fish.bounds.width, fish.bounds.height);
            }

            for (NetEffect net : activeNets) {
                shapeRenderer.rect(net.position.x - net.radius, net.position.y - net.radius, net.radius * 2f, net.radius * 2f);
            }

            // Hitbox matches true rendered size: width is scaled by cannonScaleX, not the full square
            float hitW = playerWidth * cannonScaleX;
            shapeRenderer.rect(
                playerPos.x - hitW / 2f,
                playerPos.y - playerHeight / 2f,
                hitW,
                playerHeight
            );

            shapeRenderer.end();
        }

        // Render UI
        scoreboard.update(delta);
        scoreboard.render();
    }

    @Override
    public void resize(int width, int height) {
        // Adjust the viewport when screen resizes to maintain proportion
        viewport.update(width, height, true);
        scoreboard.resize(width, height);
    }

    private void spawnFish() {
        // Weighted Spawning
        float roll = MathUtils.random();
        FishType type;
        if (roll < 0.70f) {
            type = FishType.SMALL;
        } else if (roll < 0.95f) {
            type = FishType.MEDIUM;
        } else {
            type = FishType.LARGE;
        }

        int edge = MathUtils.random(0, 3); // 0: Top, 1: Bottom, 2: Left, 3: Right
        float startX = 0, startY = 0;

        // The Spawn Buffer: All fish must spawn at least 150 pixels outside the 1280x720 frame
        switch (edge) {
            case 0: // Top
                startX = MathUtils.random(100f, WORLD_WIDTH - 100f);
                startY = 870f;
                break;
            case 1: // Bottom
                startX = MathUtils.random(100f, WORLD_WIDTH - 100f);
                startY = -150f;
                break;
            case 2: // Left
                startX = -150f;
                startY = MathUtils.random(100f, WORLD_HEIGHT - 100f);
                break;
            case 3: // Right
                startX = 1430f;
                startY = MathUtils.random(100f, WORLD_HEIGHT - 100f);
                break;
        }
        
        IMovementStrategy strategy = linearMovement; // Replacing sinusoidal permanently with linear per request
        
        int swarmCount = MathUtils.random(type.swarmMin, type.swarmMax);
        
        // Factory compatibility logic matching polymorphic references directly utilizing separate pools 
        BaseFish leader;
        if (type == FishType.SMALL) leader = clownfishPool.obtain();
        else if (type == FishType.MEDIUM) leader = lionfishPool.obtain();
        else leader = mantaRayPool.obtain();
        leader.init(startX, startY, 0f, 0f, assets.placeholderTexture, strategy, type); 
        activeFishes.add(leader);
        
        float leaderVx = leader.velocity.x;
        float leaderVy = leader.velocity.y;
        Vector2 dir = new Vector2(leaderVx, leaderVy).nor(); // Normalized direction

        com.badlogic.gdx.utils.Array<Vector2> placements = new com.badlogic.gdx.utils.Array<>();
        placements.add(new Vector2(0, 0)); // Leader is at offset (0,0)

        // Spawn consecutive followers using organic "school of fish" clustering logic
        for (int i = 1; i < swarmCount; i++) {
            Vector2 offset = new Vector2();
            boolean valid = false;
            int attempts = 0;
            
            // As the school gets larger, gracefully allow further offsets
            float baseRadiusMax = type.size * 2.5f + (i * type.size * 0.4f);
            
            while (!valid && attempts < 50) {
                attempts++;
                
                // Form a teardrop / oblong organic cluster shaped tightly towards movement direction
                float randomAngle = MathUtils.random(0f, MathUtils.PI2);
                // Jitter distribution scaled to look like fish avoiding perfect grids
                float randomDist = baseRadiusMax * (float)Math.sqrt(MathUtils.random(0f, 1f)); 
                
                float perpX = -dir.y;
                float perpY = dir.x;
                
                // Scale distance differently along the leading direction vector and the width
                float alongForward = dir.x * (randomDist * MathUtils.cos(randomAngle) * 1.6f);
                float alongUpward = dir.y * (randomDist * MathUtils.cos(randomAngle) * 1.6f);
                
                // Keep the swarm tighter on its flanks (cross section perpendicular to movement)
                float acrossLateral = perpX * (randomDist * MathUtils.sin(randomAngle) * 0.9f);
                float acrossVertical = perpY * (randomDist * MathUtils.sin(randomAngle) * 0.9f);
                
                offset.set(alongForward + acrossLateral, alongUpward + acrossVertical);
                
                // Anti-Overlap Logic: Ensure natural minimum spacing of at least 1.5 * fishSize
                valid = true;
                for (Vector2 p : placements) {
                    if (p.dst(offset) < type.size * 1.5f) {
                        valid = false;
                        break;
                    }
                }
                
                if (!valid) {
                    // Margin gets slightly more relaxed the deeper in attempts we iterate
                    baseRadiusMax += type.size * 0.1f; 
                }
            }
            
            placements.add(new Vector2(offset));
            
            BaseFish follower;
            if (type == FishType.SMALL) follower = clownfishPool.obtain();
            else if (type == FishType.MEDIUM) follower = lionfishPool.obtain();
            else follower = mantaRayPool.obtain();
            // Supply existing velocity directly via init() to freeze swarm trajectory completely
            follower.init(startX + offset.x, startY + offset.y, leaderVx, leaderVy, assets.placeholderTexture, strategy, type); 
            activeFishes.add(follower);
        }
    }

    @Override
    public void onFishCaught(CatchEvent event) {
        // Obsolete override: PlayScreen manages collision natively, the Scoreboard is the observer
    }

    public void handleCollision(BaseFish fish) {
        float roll = MathUtils.random(); // 0.0 to 1.0 (inclusive of 0, exclusive of 1)
        
        if (roll <= fish.type.deathProbability) {
            // First: Create the event data payload decoupled from the object ref
            CatchEvent event = new CatchEvent(
                fish.type.points, 
                fish.type.name(), 
                fish.type.color, 
                fish.position.x, 
                fish.position.y
            );
            
            // Second: Notify Observer BEFORE nullifying the entity instances
            scoreboard.onFishCaught(event);
            
            // Spawn Coins
            int coinCount = fish.type.points / 10;
            if (coinCount < 1) coinCount = 1;
            // Cap at 10 coins max for visual clarity
            if (coinCount > 10) coinCount = 10; 
            int pointsPerCoin = fish.type.points / coinCount;
            
            for (int i = 0; i < coinCount; i++) {
                Coin coin = coinPool.obtain();
                coin.init(fish.position.x, fish.position.y, pointsPerCoin);
                activeCoins.add(coin);
            }
            
            // Third: Now free the fish properly and safely isolate metrics
            fish.active = false;
            activeFishes.removeValue(fish, true);
            if (fish instanceof Clownfish) {
                clownfishPool.free((Clownfish) fish);
            } else if (fish instanceof Lionfish) {
                lionfishPool.free((Lionfish) fish);
            } else if (fish instanceof MantaRay) {
                mantaRayPool.free((MantaRay) fish);
            }
            
            // Log action (will be sent to backend later)
            Gdx.app.log("Game", event.fishTypeName + " Fish killed at (" + event.x + ", " + event.y + ")");
        } else {
            // Fish survives
            Gdx.app.log("Game", fish.type + " Fish SURVIVED hit at " + fish.position);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (scoreboard != null) {
            scoreboard.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}

