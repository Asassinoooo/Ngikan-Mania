package com.ngikanmania.observer;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

/**
 * Observer Pattern: Listener for fish events.
 */
public interface FishObserver {
    void onFishCaught(CatchEvent event);
}

