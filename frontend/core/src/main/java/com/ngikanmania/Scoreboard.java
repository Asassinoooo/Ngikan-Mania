package com.ngikanmania;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern: Acts as the Game UI, syncing internal stats securely.
 * Contains the Command action queue.
 */
public class Scoreboard implements FishObserver {
    private final Stage stage;
    private float points;

    private Label pointsLabel;

    // Local queued commands for the backend
    private final List<GameActionCommand> commandQueue;

    // Visual Feedback (Floating Texts)
    public static class FloatingText {
        public float x, y, timeAlive;
        public String text;
        public Label label;
    }

    private final Pool<FloatingText> floatingTextPool;
    private final Array<FloatingText> activeFloatingTexts;

    public Scoreboard() {
        commandQueue = new ArrayList<>();
        stage = new Stage(new ScreenViewport());

        points = 200f; // Unified points variable starting at 200

        BitmapFont font = Assets.getInstance().font;
        Label.LabelStyle defaultStyle = new Label.LabelStyle(font, Color.WHITE);

        pointsLabel = new Label("Points: " + (int)points, defaultStyle);

        Table topTable = new Table();
        topTable.top();
        topTable.setFillParent(true);

        topTable.add(pointsLabel).expandX().align(Align.right).padRight(20).padTop(20);

        stage.addActor(topTable);

        // Object Pool for fading texts avoiding GC lag
        activeFloatingTexts = new Array<>();
        floatingTextPool = new Pool<FloatingText>(20) {
            @Override
            protected FloatingText newObject() {
                FloatingText ft = new FloatingText();
                ft.label = new Label("", defaultStyle);
                return ft;
            }
        };
    }

    public float getPoints() {
        return points;
    }

    public void onBulletFired(float originX, float originY, float vx, float vy) {
        points -= 1f;
        pointsLabel.setText("Points: " + (int)points);
        
        GameActionCommand deductCmd = new PointDeductionCommand(1f);
        deductCmd.execute();
        commandQueue.add(deductCmd);

        GameActionCommand cmd = new FireBulletCommand(originX, originY, vx, vy);
        cmd.execute();
        commandQueue.add(cmd);
    }

    @Override
    public void onFishCaught(CatchEvent event) {
        int earned = event.points;
        points += earned;
        pointsLabel.setText("Points: " + (int)points);

        // Command Logic mapping Enum natively safely via String reconstruction bounds
        FishType parsedType = FishType.valueOf(event.fishTypeName);
        GameActionCommand catchCmd = new CatchFishCommand(parsedType, earned);
        catchCmd.execute();
        commandQueue.add(catchCmd);

        // Visual Feedback (floating text) mapped precisely without relying on GC culled entity memory references
        FloatingText ft = floatingTextPool.obtain();
        ft.text = "+" + earned;
        ft.x = event.x + 16f; // Roughly size offset (32/2) 
        ft.y = event.y + 16f; 
        ft.timeAlive = 0;
        ft.label.setText(ft.text);
        ft.label.setColor(event.fishColor); // Match color dynamically 
        ft.label.setPosition(ft.x, ft.y);
        activeFloatingTexts.add(ft);
        stage.addActor(ft.label);
    }

    public void update(float delta) {
        // Run floating text logic
        for (int i = activeFloatingTexts.size - 1; i >= 0; i--) {
            FloatingText ft = activeFloatingTexts.get(i);
            ft.timeAlive += delta;

            // Float upwards slightly
            ft.y += 30f * delta;
            ft.label.setPosition(ft.x, ft.y);

            // Fade out
            float alpha = 1.0f - (ft.timeAlive / 1.0f); // 1 sec max life
            if (alpha <= 0) {
                ft.label.remove();
                activeFloatingTexts.removeIndex(i);
                floatingTextPool.free(ft);
            } else {
                Color c = ft.label.getColor();
                c.a = alpha;
                ft.label.setColor(c);
            }
        }
        
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }

    public List<GameActionCommand> getCommandQueue() {
        return commandQueue;
    }
}
