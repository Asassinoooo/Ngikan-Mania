package com.ngikanmania.strategy;

import com.ngikanmania.core.*;
import com.ngikanmania.entity.*;
import com.ngikanmania.strategy.*;
import com.ngikanmania.command.*;
import com.ngikanmania.observer.*;

/**
 * Strategy Pattern: Movement strategy for entities.
 */
public interface IMovementStrategy {
    void init(BaseFish entity);
    boolean update(BaseFish entity, float delta);
}

