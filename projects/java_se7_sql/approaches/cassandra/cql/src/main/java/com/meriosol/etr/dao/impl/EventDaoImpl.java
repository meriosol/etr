package com.meriosol.etr.dao.impl;

import com.datastax.driver.core.*;
import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;
import com.meriosol.exception.EtrException;
import com.meriosol.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC impl for Event DAO.
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class EventDaoImpl implements EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDaoImpl.class);
    private static final Integer DEFAULT_MAX_EVENT_COUNT = 20; // normally client should query max 100
    private static final String EVENT_COLUMNS = "id, title, category_code, severity, source, process_id, " + EventEntityNames.Column.CREATED;
    public static final int TIME = 100 * 60 * 60 * 24 * 365 * 10;
    private DbSessionFactory dbSessionFactory;
    private EventCategoryCache eventCategoryCache;
    private EventByCreatedSliceCrud eventByCreatedSliceCrud;

    interface EventEntityNames {
        String TABLE = "events";

        interface Column {
            String CREATED = "created";
        }
    }


    public EventDaoImpl() {
        this.dbSessionFactory = DbSessionFactory.getInstance();
        this.eventCategoryCache = EventCategoryCache.getInstance();
        this.eventByCreatedSliceCrud = EventByCreatedSliceCrud.getInstance();
    }

    @Override
    public String getDaoName() {
        return "CasandraCqlImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Casandra/CQL implementaion";
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
        Session session = this.dbSessionFactory.obtainNewDefaultSession();
        if (session == null) {
            throw new EtrException(module + " - Session should not be null!");
        }

        Event result = event; // TODO: ideally sh be cloned..

        String keyspace = this.dbSessionFactory.obtainDefaultKeyspace();
        String insertDml = "INSERT INTO " + getFullEventTableName(keyspace) + " (" + EVENT_COLUMNS + ") VALUES (?,?,?,?,?,?,?);";
        PreparedStatement statement = session.prepare(insertDml);

        // ID:
        Long id = event.getId();
        if (id == null) {
            id = IdGenerator.generateNewId();
        }

        // Category:
        Event.Category category = event.getCategory();
        String categoryCode = null;
        if (category != null) {
            categoryCode = category.getCode();
        }

        // Severity:
        String severityCode = null;
        Event.Severity eventSeverity = event.getSeverity();
        if (eventSeverity != null) {
            severityCode = eventSeverity.name();
        }

        // Created:
        Long createdTime;
        Date created = event.getCreated();
        if (created == null) {
            created = new Date();
        }
        createdTime = created.getTime();

        BoundStatement boundStatement = statement.bind(id, event.getTitle(), categoryCode, severityCode
                , event.getSource(), event.getProcessId(), createdTime);
        ResultSet insertResults = session.execute(boundStatement);

        // 2. Insert into event by created slices:
        this.eventByCreatedSliceCrud.insertByCreatedSlice(createdTime, id, session, keyspace);

        // 3. Finally update resulting event POJO:
        result.setId(id);
        result.setCreated(created);

        return result;
    }

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    @Override
    public Event retrieveEvent(Long eventId) {
        return retrieveEvent(eventId, this.dbSessionFactory.obtainNewDefaultSession(), this.dbSessionFactory.obtainDefaultKeyspace());
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
    public List<Event> retrieveRecentEvents(Integer maxEventCount) {
        final String module = "retrieveRecentEvents";
        Session session = this.dbSessionFactory.obtainNewDefaultSession();
        if (session == null) {
            throw new EtrException(module + " - Session should not be null!");
        }
        if (maxEventCount == null) {
            maxEventCount = DEFAULT_MAX_EVENT_COUNT;
        } else if (maxEventCount <= 0) {
            throw new IllegalArgumentException(module + " - maxEventCount should be > 0!");
        }

        String keyspace = this.dbSessionFactory.obtainDefaultKeyspace();
        List<Long> recentEventIds = this.eventByCreatedSliceCrud.loadRecentEventIds(maxEventCount, session, keyspace);

        return retrieveEventsByIds(recentEventIds, session, keyspace);
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
        Session session = this.dbSessionFactory.obtainNewDefaultSession();
        if (session == null) {
            throw new EtrException("Session should not be null!");
        }
        if (startDate != null && endDate != null) {
            if (startDate.getTime() >= endDate.getTime()) {
                throw new IllegalArgumentException("startDate should be < endDate!");
            }
        }
        if (startDate == null) {
            startDate = new Date(System.currentTimeMillis() - TIME);
        }
        if (endDate == null) {
            endDate = new Date();
        }

        String keyspace = this.dbSessionFactory.obtainDefaultKeyspace();
        Long timeFrom = startDate.getTime();
        Long timeTo = endDate.getTime();
        int limit = DEFAULT_MAX_EVENT_COUNT;

        List<Long> recentEventIds = this.eventByCreatedSliceCrud.loadEventsForPeriod(timeFrom, timeTo, limit, session, keyspace);
        return retrieveEventsByIds(recentEventIds, session, keyspace);
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
        Session session = this.dbSessionFactory.obtainNewDefaultSession();
        if (session == null) {
            throw new EtrException(module + " - Session should not be null!");
        }

        Event result = event; // TODO: ideally sh be cloned..

        String keyspace = this.dbSessionFactory.obtainDefaultKeyspace();
        String updateTitleDml = "UPDATE " + getFullEventTableName(keyspace)
                + " SET title = ?, category_code = ?, severity = ?, source = ?, process_id = ? where id = ?;";

        LOG.info("Event with id = '{}' is about to be updated. CQL: {}", event.getId(), updateTitleDml);
        PreparedStatement statement = session.prepare(updateTitleDml);

        // Category:
        Event.Category category = event.getCategory();
        String categoryCode = null;
        if (category != null) {
            categoryCode = category.getCode();
        }

        // Severity:
        String severityCode = null;
        Event.Severity eventSeverity = event.getSeverity();
        if (eventSeverity != null) {
            severityCode = eventSeverity.name();
        }

        BoundStatement boundStatement = statement.bind(event.getTitle(), categoryCode, severityCode
                , event.getSource(), event.getProcessId(), event.getId());

        ResultSet updatedResults = session.execute(boundStatement);

        return result;
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
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }
        Session session = this.dbSessionFactory.obtainNewDefaultSession();
        if (session == null) {
            throw new EtrException(module + " - Session should not be null!");
        }
        String keyspace = this.dbSessionFactory.obtainDefaultKeyspace();
        Long createdTime = retrieveEventCreated(eventId, session, keyspace);

        String deleteDml = "DELETE FROM " + getFullEventTableName(keyspace) + " where id = ?;";
        PreparedStatement statement = session.prepare(deleteDml);
        BoundStatement boundStatement = statement.bind(eventId);
        session.execute(boundStatement);

        // Also delete entry from slices:
        this.eventByCreatedSliceCrud.deleteEventByCreatedSlice(eventId, createdTime, session, keyspace);
    }

    //--------------------------------
    // Utils:

    /**
     * @param eventId
     * @param session
     * @param keyspace
     * @return
     */
    private Event retrieveEvent(Long eventId, Session session, String keyspace) {
        final String module = "retrieveEvent";
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }
        if (session == null) {
            throw new IllegalArgumentException(module + " - Session should not be null!");
        }
        if (keyspace == null) {
            throw new IllegalArgumentException(module + " - Keyspace should not be null!");
        }

        String selector = "SELECT " + EVENT_COLUMNS
                + " FROM " + getFullEventTableName(keyspace) + " WHERE id = ? LIMIT 1;";

        PreparedStatement statement = session.prepare(selector);
        LOG.info("BEFORE Retrieve event by eventId='{}'. Selector: {}", eventId, selector);

        BoundStatement boundStatement = statement.bind(eventId);
        ResultSet results = session.execute(boundStatement);
        Row eventRow = null;
        if (results != null) {
            eventRow = results.one(); //TODO: what if >1?
        }

        return buildEventFromResultSet(eventRow, session, keyspace);
    }

    private Long retrieveEventCreated(Long eventId, Session session, String keyspace) {
        final String module = "retrieveEventCreated";
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }
        if (session == null) {
            throw new IllegalArgumentException(module + " - Session should not be null!");
        }
        if (keyspace == null) {
            throw new IllegalArgumentException(module + " - Keyspace should not be null!");
        }

        String selector = "SELECT " + EventEntityNames.Column.CREATED + " FROM " + getFullEventTableName(keyspace) + " WHERE id = ? LIMIT 1;";

        PreparedStatement statement = session.prepare(selector);
        LOG.info("BEFORE Retrieve event.created by eventId='{}'. Selector: {}", eventId, selector);

        BoundStatement boundStatement = statement.bind(eventId);
        ResultSet results = session.execute(boundStatement);
        Long createdTime = null;
        if (results != null) {
            Row eventRow = results.one(); //TODO: what if >1?
            if (eventRow != null) {
                createdTime = eventRow.getLong(EventEntityNames.Column.CREATED);
            }
        }

        return createdTime;
    }


    /**
     * @param recentEventIds
     * @param session
     * @param keyspace
     * @return
     */
    private List<Event> retrieveEventsByIds(List<Long> recentEventIds, Session session, String keyspace) {
        List<Event> events = null;
        if (recentEventIds != null) {
            String eventIdsStr = Util.join(recentEventIds, ",");

            String selector = "SELECT " + EVENT_COLUMNS
                    + " FROM " + getFullEventTableName(keyspace) + " WHERE id IN (" + eventIdsStr + ");";

            LOG.info("BEFORE Retrieve events. Selector: {}", selector);
            ResultSet eventsRS = session.execute(selector);
            if (eventsRS != null) {
                events = new ArrayList<>();
                for (Row eventRow : eventsRS) {
                    events.add(buildEventFromResultSet(eventRow, session, keyspace));
                }
            }

        }
        return events;

    }

    /**
     * @param eventRow
     * @param session
     * @param keyspace
     * @return
     */
    private Event buildEventFromResultSet(Row eventRow, Session session, String keyspace) {
        Event result = null;

        if (eventRow != null) {
            result = new Event();
            result.setId(eventRow.getLong("id"));
            result.setTitle(eventRow.getString("title"));

            // Set Category
            String categoryCode = eventRow.getString("category_code");
            if (categoryCode != null) {
                Event.Category category = this.eventCategoryCache.lookupCategory(categoryCode, session, keyspace);
                result.setCategory(category);
            }

            // Set Severity
            String severityCode = eventRow.getString("severity");
            if (severityCode != null) {
                Event.Severity severity = Event.Severity.valueOf(severityCode);
                if (severity != null) {
                    result.setSeverity(severity);
                }
            }

            result.setSource(eventRow.getString("source"));
            result.setProcessId(eventRow.getString("process_id"));

            result.setCreated(new Date(eventRow.getLong(EventEntityNames.Column.CREATED)));
        }

        return result;
    }

    /**
     * @param keyspace
     * @return
     */
    private static String getFullEventTableName(String keyspace) {
        return keyspace
                + "." + EventEntityNames.TABLE;
    }

}
