package com.ngikanmania;

/**
 * Command Pattern: Event encapsulated for catching a fish.
 */
public class CatchFishCommand implements GameActionCommand {
    private final FishType fishType;
    private final int pointsAwarded;
    // We can add timestamps etc later for backend syncing

    public CatchFishCommand(FishType fishType, int pointsAwarded) {
        this.fishType = fishType;
        this.pointsAwarded = pointsAwarded;
    }

    @Override
    public void execute() {
        // Will be used later to send this event to the backend server via REST
        System.out.println("Command Executed: Fish Caught! Type: " + fishType + ", Points: " + pointsAwarded);
    }
}
