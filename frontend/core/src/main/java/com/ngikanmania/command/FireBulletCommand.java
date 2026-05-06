package com.ngikanmania.command;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

/**
 * Command Pattern: Event encapsulated for firing a bullet.
 */
public class FireBulletCommand implements GameActionCommand {
    private final float startX, startY;
    private final float velocityX, velocityY;

    public FireBulletCommand(float startX, float startY, float velocityX, float velocityY) {
        this.startX = startX;
        this.startY = startY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public void execute() {
        // Here we'd package and stage an event for the backend
        System.out.println("Command Executed: Fired Bullet from (" + startX + ", " + startY + ")");
    }
}

