package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.dao.mapper.EventMapper;
import com.meriosol.etr.domain.Event;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Mybatis (XML) impl for Event DAO.
 *
 * @author meriosol
 * @version 0.1
 * @since 22/01/14
 */
public class EventDaoImpl implements EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDaoImpl.class);
    private static final Long DEFAULT_MAX_EVENT_COUNT = 10000L; // normally client should query max 100
    private static final String MYBATIS_RESOURCE_CONFIG = "com/meriosol/etr/dao/mybatis-config.xml";
    private SqlSessionFactory sessionFactory;
    private EventCategoryCache eventCategoryCache;

    public EventDaoImpl() {
        initSessionFactory();
        this.eventCategoryCache = EventCategoryCache.getInstance();
    }

    @Override
    public String getDaoName() {
        return "MybatisXmlImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Mybatis Xml implementation";
    }

    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     * Output invariant1: id should appear in returned object event.<br>
     *
     * @param event
     * @return Created event.
     */
    @Override
    public Event create(Event event) {
        final String module = "create";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }

        try (SqlSession session = this.sessionFactory.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
            EventMapper mapper = session.getMapper(EventMapper.class);
            int howManyRowsInserted = mapper.create(event);
            assert howManyRowsInserted == 1;
            session.commit();
            LOG.debug("AFTER commit: ID for created event ='{}', created='{}'..", event.getId(), event.getCreated());
        }
        return event;
    }

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    @Override
    public Event retrieveEvent(Long eventId) {
        Event event;
        try (SqlSession session = this.sessionFactory.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
            EventMapper positionMapper = session.getMapper(EventMapper.class);
            event = positionMapper.retrieveEvent(eventId);
            session.commit();
        }
        return event;
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
    public List<Event> retrieveRecentEvents(Long maxEventCount) {
        final String module = "retrieveRecentEvents";
        if (maxEventCount == null) {
            maxEventCount = DEFAULT_MAX_EVENT_COUNT;
        } else if (maxEventCount <= 0) {
            throw new IllegalArgumentException(module + " - maxEventCount should be > 0!");
        }
        throw new RuntimeException("Not implemented!");

//        List<Event> events = new ArrayList<>();
//        return events;
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
    public List<Event> retrieveEventsForPeriod(Date startDate, Date endDate) {
        final String module = "retrieveEventsForPeriod";
        if (startDate != null && endDate != null) {
            if (startDate.getTime() >= endDate.getTime()) {
                throw new IllegalArgumentException("startDate should be < endDate!");
            }
        } else if (startDate != null) {
        } else if (endDate != null) {
        }
        throw new RuntimeException("Not implemented!");

//        List<Event> events = new ArrayList<>();
//        return events;
    }

    /**
     * @param event
     * @return Updated event(normally exactly the same object).
     */
    @Override
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
        throw new RuntimeException("Not implemented!");
    }

    /**
     * Deletes event.
     *
     * @param event
     */
    @Override
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
    public void deleteEvent(Long eventId) {
        final String module = "deleteEvent";
        throw new RuntimeException("Not implemented!");
    }

    //--------------------------------
    // Utils:
    private void initSessionFactory() {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(MYBATIS_RESOURCE_CONFIG);
            this.sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            LOG.error(String.format("Error while opening inputStream for resource '%s'!", MYBATIS_RESOURCE_CONFIG), e);
        }
    }


}
