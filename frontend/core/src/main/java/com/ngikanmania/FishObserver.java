package com.ngikanmania;

/**
 * Observer Pattern: Listener for fish events.
 */
public interface FishObserver {
    void onFishCaught(CatchEvent event);
}
