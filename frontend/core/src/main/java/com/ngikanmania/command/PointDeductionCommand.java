package com.ngikanmania.command;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

/**
 * Command Pattern: Event encapsulated for deducting points when a bullet is fired.
 */
public class PointDeductionCommand implements GameActionCommand {
    private final float pointsDeducted;

    public PointDeductionCommand(float pointsDeducted) {
        this.pointsDeducted = pointsDeducted;
    }

    @Override
    public void execute() {
        System.out.println("Command Executed: Deducted " + pointsDeducted + " points for firing bullet.");
    }
}

