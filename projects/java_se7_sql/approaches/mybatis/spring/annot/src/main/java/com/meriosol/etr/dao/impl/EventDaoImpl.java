package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.dao.mapper.EventMapper;
import com.meriosol.etr.domain.Event;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring with mybatis impl for Event DAO.
 *
 * @author meriosol
 * @version 0.1
 * @since 22/01/14
 */
@Service("EventDaoService")
public class EventDaoImpl implements EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDaoImpl.class);
    private static final Integer DEFAULT_MAX_EVENT_COUNT = 20; // normally client should query max 100
    public static final int SOME_TIME = 100 * 60 * 60 * 24 * 365 * 10;

    @Autowired
    private EventMapper eventMapper;

    public EventDaoImpl() {
    }

    @Override
    public String getDaoName() {
        return "MybatisAnnotSpringImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Mybatis annotation Spring implementation";
    }

    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     * Output invariant1: id should appear in returned object event.<br>
     *
     * @param event
     * @return Created event.
     */
    @Override
    @Transactional
    public Event create(Event event) {
        final String module = "create";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }

        int howManyRowsInserted = this.eventMapper.create(event);
        assert howManyRowsInserted == 1;
        LOG.debug("AFTER commit: ID for created event ='{}', created='{}'..", event.getId(), event.getCreated());
        return event;
    }

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    @Override
    @Transactional
    public Event retrieveEvent(Long eventId) {
        return eventMapper.retrieveEvent(eventId);
    }

    /**
     * Output invariant1: Sorting should be by field 'created' desc.<br>
     * Output invariant2:  should be List.size <= maxEventCount.<br>
     *
     * @param maxEventCount How many event to return.<br>
     *                      NOTE: If null provided, all events to be returned (CAUTION: dangerous case if DB is very large).
     * @return List of events.
     */
    @Override
    @Transactional
    public List<Event> retrieveRecentEvents(Integer maxEventCount) {
        final String module = "retrieveRecentEvents";
        if (maxEventCount == null) {
            maxEventCount = DEFAULT_MAX_EVENT_COUNT;
        } else if (maxEventCount <= 0) {
            throw new IllegalArgumentException(module + " - maxEventCount should be > 0!");
        }

        List<Event> events;
        int offset = 1;
        int limit = Integer.MAX_VALUE;
        if (maxEventCount < Integer.MAX_VALUE) {
            limit = maxEventCount;
        }
        RowBounds rowBounds = new RowBounds(offset, limit);
        events = this.eventMapper.retrieveRecentEvents(rowBounds);

        return events;
    }

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
    @Override
    @Transactional
    public List<Event> retrieveEventsForPeriod(Date startDate, Date endDate) {
        // TODO: consider cases:
        // (startDate == null, endDate == null)
        // (startDate == null, endDate != null)
        // (startDate != null, endDate == null)
        // (startDate != null, endDate != null)
        // NOTE: for now artificial values are to be set in cease of nulls
        if (startDate != null && endDate != null) {
            if (startDate.getTime() >= endDate.getTime()) {
                throw new IllegalArgumentException("startDate should be < endDate!");
            }
        }
        if (startDate == null) {
            startDate = new Date(System.currentTimeMillis() - SOME_TIME);
        }
        if (endDate == null) {
            endDate = new Date();
        }
        List<Event> events;

        Map<String, Object> params = new HashMap<>(2);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        events = this.eventMapper.retrieveEventsForPeriod(params);

        return events;
    }

    /**
     * @param event
     * @return Updated event(normally exactly the same object).
     */
    @Override
    @Transactional
    public Event update(Event event) {
        final String module = "update";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getId() == null) {
            throw new IllegalArgumentException(module + " - Event ID should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }
        int howManyRowsUpdated = this.eventMapper.update(event);
        assert howManyRowsUpdated == 1;
        LOG.debug("AFTER commit: ID for updated event ='{}', created='{}'..", event.getId(), event.getCreated());
        return event;
    }

    /**
     * Deletes event.
     *
     * @param event
     */
    @Override
    @Transactional
    public void delete(Event event) {
        if (event != null) {
            deleteEvent(event.getId());
        }
    }

    /**
     * Deletes event.
     *
     * @param eventId
     */
    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        int howManyRowsUpdated = this.eventMapper.delete(eventId);
        assert howManyRowsUpdated == 1;
    }

}
