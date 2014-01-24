package com.meriosol.etr.dao.mapper;

import com.meriosol.etr.domain.Event;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     * Output invariant1: Sorting should be by field 'created' desc.<br>
     * Output invariant2:  should be List.size <= maxEventCount.<br>
     *
     * @param rowBounds pagination object.<br>
     *                      NOTE: If null provided, all events to be returned (CAUTION: dangerous case if DB is very large).
     * @return List of events.
     */
    List<Event> retrieveRecentEvents(RowBounds rowBounds);

    /**
     * Input params invariant1: if <code>startDate</code> and <code>endDate</code> are not null, startDate must be < endDate.<br>
     * Input params invariant2: if <code>startDate</code> and <code>endDate</code> are null, all events to be returned (CAUTION: dangerous case if DB is very large).<br>
     * Output invariant1: Sorting should be by field 'created' desc.<br>
     * Output invariant2: Field 'created' should have value in range (startDate, endDate].<br>
     *
     * @param params (for now: startDate - Start date (nullable, exclusive), endDate - End date (nullable, inclusive))
     * @return List of events.
     */
    List<Event> retrieveEventsForPeriod(Map<String, Object> params);

    /**
     * @param event
     * @return how many rows were updated (normally should be 1).
     */
    int update(Event event);

    /**
     * Deletes event.
     *
     * @param eventId
     * @return how many rows were deleted (normally should be 1).
     */
    int delete(Long eventId);

}
