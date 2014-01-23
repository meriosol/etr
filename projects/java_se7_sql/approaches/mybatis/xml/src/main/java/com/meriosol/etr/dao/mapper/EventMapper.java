package com.meriosol.etr.dao.mapper;

import com.meriosol.etr.domain.Event;

/**
 * @author meriosol
 * @version 0.1
 * @since 22/01/14
 */
public interface EventMapper {
    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     *
     * @param event
     * @return Created event ID.
     */
    Long create(Event event);

}
