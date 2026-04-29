package com.ngikanmania;

/**
 * Strategy Pattern: Movement strategy for entities.
 */
public interface IMovementStrategy {
    void init(BaseFish entity);
    boolean update(BaseFish entity, float delta);
}
