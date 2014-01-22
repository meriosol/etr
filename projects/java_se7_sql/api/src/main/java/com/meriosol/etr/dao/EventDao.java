package com.meriosol.etr.dao;

import com.meriosol.etr.domain.Event;

import java.util.Date;
import java.util.List;

/**
 * Event DAO.<br>
 * NOTE: Contract based approach(http://en.wikipedia.org/wiki/Design_by_contract)
 * is "implemented" via providing input and output invariants in method docs. Impls and tests should validate these variants.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public interface EventDao  extends BaseDao {
    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     * Output invariant1: id should appear in returned object event.<br>
     *
     * @param event
     * @return Created event.
     */
    Event create(Event event);

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    Event retrieveEvent(Long eventId);

    /**
     * Output invariant1: Sorting should be by field 'created' desc.<br>
     * Output invariant2:  should be List.size <= maxEventCount.<br>
     *
     * @param maxEventCount How many event to return.<br>
     *                      NOTE: If null provided, all events to be returned (CAUTION: dangerous case if DB is very large).
     * @return List of events.
     */
    List<Event> retrieveRecentEvents(Long maxEventCount);

    /**
     * Input params invariant1: if <code>startDate</code> and <code>endDate</code> are not null, startDate must be < endDate.<br>
     * Input params invariant2: if <code>startDate</code> and <code>endDate</code> are null, all events to be returned (CAUTION: dangerous case if DB is very large).<br>
     * Output invariant1: Sorting should be by field 'created' desc.<br>
     * Output invariant2: Field 'created' should have value in range (startDate, endDate].<br>
     *
     * @param startDate Start date (nullable, exclusive)
     * @param endDate   End date (nullable, inclusive)
     * @return List of events.
     */
    List<Event> retrieveEventsForPeriod(Date startDate, Date endDate);

    /**
     * @param event
     * @return Updated event(normally exactly the same object).
     */
    Event update(Event event);

    /**
     * Deletes event.
     *
     * @param event
     */
    void delete(Event event);

    /**
     * Deletes event.
     *
     * @param eventId
     */
    void deleteEvent(Long eventId);
}
