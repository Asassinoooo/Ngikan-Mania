package com.ngikanmania.command;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

/**
 * Command Pattern: Encapsulates user actions and system events.
 */
public interface GameActionCommand {
    void execute();
}

