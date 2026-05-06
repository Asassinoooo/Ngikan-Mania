package com.ngikanmania.core;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class NgikanMania extends Game {

    @Override
    public void create() {
        // Init Singleton Assets explicitly
        Assets.getInstance();
        // State Pattern: Delegate rendering to the PlayScreen State
        this.setScreen(new PlayScreen());
    }

    @Override
    public void dispose() {
        super.dispose(); // disposes the active screen
        Assets.getInstance().dispose();
    }
}

