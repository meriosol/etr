package com.meriosol.etr.dao.mapper;

import com.meriosol.etr.domain.Event;

/**
 * NOTE: This interface resembles <code>EventDao</code>, but with tiny signature deviations.
 * @author meriosol
 * @version 0.1
 * @since 22/01/14
 */
public interface EventMapper {
    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     *
     * @param event
     * @return How many rows got created. NOTE: both ID and title should be present in <code>event</code> object after insert.
     */
    int create(Event event);

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    public Event retrieveEvent(Long eventId);
}
