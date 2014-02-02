package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;
import com.meriosol.exception.EtrException;
import com.meriosol.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParseException;
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
    private static final String EVENT_COLUMNS = "id, title, category_code, severity, source, process_id, created";
    private DbConnectionFactory dbConnectionFactory;
    private EventCategoryCache eventCategoryCache;

    public EventDaoImpl() {
        this.dbConnectionFactory = DbConnectionFactory.getInstance();
        this.eventCategoryCache = EventCategoryCache.getInstance();
    }

    @Override
    public String getDaoName() {
        return "JdbcImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Jdbc implementaion";
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
        Connection connection = this.dbConnectionFactory.obtainNewDefaultConnection();
        if (connection == null) {
            throw new EtrException(module + " - Connection should not be null!");
        }

        Event result = event; // TODO: ideally sh be cloned..

        // Insert:
        // INSERT INTO events (title, category_code , severity , source , process_id, created)
        //  VALUES ('Initial test1 start','START','INFO','local','00001','2014-01-10 11:03:09.173');

        String insertStatementStr = "INSERT INTO events(title, category_code, severity, source, process_id, created) VALUES (?,?,?,?,?,?)";
        LOG.info("{}: insertStatementStr: {} ", module, insertStatementStr);
        LOG.info("{}: event for insert: {}", module, event);
        boolean initialTxAutoCommit = false;
        try (PreparedStatement insertStatement = connection.prepareStatement(insertStatementStr, Statement.RETURN_GENERATED_KEYS)) {
            initialTxAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            insertStatement.setString(1, event.getTitle());
            // Insert categoryCode:
            String categoryCode = null;
            Event.Category category = event.getCategory();
            if (category != null) {
                categoryCode = category.getCode();
            }
            insertStatement.setString(2, categoryCode);

            // Insert severityCode:
            String severityCode = null;
            Event.Severity eventSeverity = event.getSeverity();
            if (eventSeverity != null) {
                severityCode = eventSeverity.name();
            }
            insertStatement.setString(3, severityCode);

            insertStatement.setString(4, event.getSource());
            insertStatement.setString(5, event.getProcessId());

            // Insert created:
            Timestamp createdAsSqlDate = null;
            Date created = event.getCreated();
            if (created != null) {
                createdAsSqlDate = new Timestamp(created.getTime());
            }
            insertStatement.setTimestamp(6, createdAsSqlDate);

            int insertedCount = insertStatement.executeUpdate();
            assert insertedCount == 1;

            // Grab generated ID:
            Long createdEventId = null;
            ResultSet rs = insertStatement.getGeneratedKeys();
            if (rs.next()) {
                createdEventId = rs.getLong(1);
            }

            // Reload only if created field was null:
            if (created == null) {
                result = retrieveEvent(createdEventId);
            } else {
                // Just set generated ID:
                result.setId(createdEventId);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new EtrException(module + " - PreparedStatement execution error!", e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    // TODO: if this reset autocommit is really needed?
                    connection.setAutoCommit(initialTxAutoCommit);
                    connection.close();
                }
            } catch (SQLException e) {
                throw new EtrException(module + " - Connection closing error!", e);
            }
        }

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
        return retrieveEvent(eventId, this.dbConnectionFactory.obtainNewDefaultConnection());
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
        Connection connection = this.dbConnectionFactory.obtainNewDefaultConnection();
        if (connection == null) {
            throw new EtrException(module + " - Connection should not be null!");
        }
        if (maxEventCount == null) {
            maxEventCount = DEFAULT_MAX_EVENT_COUNT;
        } else if (maxEventCount <= 0) {
            throw new IllegalArgumentException(module + " - maxEventCount should be > 0!");
        }
        List<Event> events = new ArrayList<>();
        String queryString = "select " + EVENT_COLUMNS + " from events order by created desc";
        try (PreparedStatement statement = connection.prepareStatement(queryString,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            LOG.info("{}: maxEventCount = {}. Query String is: '{}'", module, maxEventCount, queryString);
            ResultSet resultSet = statement.executeQuery();
            int i = 1;
            while (resultSet.next() && i++ <= maxEventCount) {
                events.add(buildEventFromResultSet(resultSet, connection));
            }
        } catch (SQLException e) {
            throw new EtrException(String.format("%s - Error while select '%s'!", module, queryString), e);
        }
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
    public List<Event> retrieveEventsForPeriod(Date startDate, Date endDate) {
        final String module = "retrieveEventsForPeriod";
        Connection connection = this.dbConnectionFactory.obtainNewDefaultConnection();
        if (connection == null) {
            throw new EtrException("Connection should not be null!");
        }
        String where = " ";
        if (startDate != null && endDate != null) {
            if (startDate.getTime() >= endDate.getTime()) {
                throw new IllegalArgumentException("startDate should be < endDate!");
            }
            where = " where created between ? and ?";
        } else if (startDate != null) {
            where = " where created > ?"; // TODO: syntax can be wrong.
        } else if (endDate != null) {
            where = " where created <= ?"; // TODO: syntax can be wrong.
        }
        List<Event> events = new ArrayList<>();
        String queryString = "select " + EVENT_COLUMNS + " from events " + where + " order by created desc";
        LOG.info("{}: queryString: '{}'", module, queryString);

        try {
            LOG.info("{}: startDate='{}'; endDate='{}', queryString: {}", module, DateUtil.formatDateWithDefaultFormat(startDate),
                    DateUtil.formatDateWithDefaultFormat(endDate), queryString);
        } catch (ParseException e) {
            LOG.error("{}: Error while parsing date!", module, e);
            LOG.info("{}: startDate='{}'; endDate='{}', queryString: {}", module, startDate, endDate, queryString);
        }

        try (PreparedStatement statement = connection.prepareStatement(queryString,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            // Set params for dates
            if (startDate != null && endDate != null) {
                statement.setTimestamp(1, new Timestamp(startDate.getTime()));
                statement.setTimestamp(2, new Timestamp(endDate.getTime()));
            } else if (startDate != null) {
                statement.setTimestamp(1, new Timestamp(startDate.getTime()));
            } else if (endDate != null) {
                statement.setTimestamp(1, new Timestamp(endDate.getTime()));
            }

            ResultSet resultSet = statement.executeQuery();
            int i = 1;
            while (resultSet.next() && i++ <= DEFAULT_MAX_EVENT_COUNT) {
                events.add(buildEventFromResultSet(resultSet, connection));
            }
        } catch (SQLException e) {
            throw new EtrException(String.format("%s - Error while select '%s' for date range('%s','%s')!"
                    , module, queryString, startDate, endDate), e);
        }
        return events;
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
        Connection connection = this.dbConnectionFactory.obtainNewDefaultConnection();
        if (connection == null) {
            throw new EtrException(module + " - Connection should not be null!");
        }

        Event result = event; // TODO: ideally sh be cloned..

        String updateStatementStr = "UPDATE events SET title = ?, category_code = ?, severity = ?, source = ?, process_id = ? where id = ?";
        // NOTE: by its nature field 'created' is immutable.
        LOG.info("{}: updateStatementStr: {}", module, updateStatementStr);

        boolean initialTxAutoCommit = false;
        try (PreparedStatement updateStatement = connection.prepareStatement(updateStatementStr)) {
            initialTxAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            updateStatement.setString(1, event.getTitle());
            // Insert categoryCode:
            String categoryCode = null;
            Event.Category category = event.getCategory();
            if (category != null) {
                categoryCode = category.getCode();
            }
            updateStatement.setString(2, categoryCode);

            // Insert severityCode:
            String severityCode = null;
            Event.Severity eventSeverity = event.getSeverity();
            if (eventSeverity != null) {
                severityCode = eventSeverity.name();
            }
            updateStatement.setString(3, severityCode);

            updateStatement.setString(4, event.getSource());
            updateStatement.setString(5, event.getProcessId());

            updateStatement.setLong(6, event.getId());

            int updatedCount = updateStatement.executeUpdate();
            assert updatedCount == 1;

            connection.commit();
        } catch (SQLException e) {
            throw new EtrException(module + " - PreparedStatement execution error!", e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    // TODO: if this reset autocommit is really needed?
                    connection.setAutoCommit(initialTxAutoCommit);
                    connection.close();
                }
            } catch (SQLException e) {
                throw new EtrException(module + " - Connection closing error!", e);
            }
        }

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
        Connection connection = this.dbConnectionFactory.obtainNewDefaultConnection();
        if (connection == null) {
            throw new EtrException(module + " - Connection should not be null!");
        }
        String deleteStatementStr = "DELETE FROM events WHERE id = ?";
        LOG.info("{}: deleteStatementStr: {}", module, deleteStatementStr);
        boolean initialTxAutoCommit = false;
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteStatementStr)) {
            initialTxAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            deleteStatement.setLong(1, eventId);

            int deletedCount = deleteStatement.executeUpdate();
            assert deletedCount == 1;

            connection.commit();
        } catch (SQLException e) {
            throw new EtrException(module + " - PreparedStatement execution error!", e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    // TODO: if this reset autocommit is really needed?
                    connection.setAutoCommit(initialTxAutoCommit);
                    connection.close();
                }
            } catch (SQLException e) {
                throw new EtrException(module + " - Connection closing error!", e);
            }
        }


    }

    //--------------------------------
    // Utils:

    private Event retrieveEvent(Long eventId, Connection connection) {
        final String module = "retrieveEvent";
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }
        if (connection == null) {
            throw new IllegalArgumentException(module + " - Connection should not be null!");
        }
        Event result = null;
        String queryString = "select id, title,category_code, severity, source, process_id, created from events where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(queryString,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setLong(1, eventId);
            LOG.info("{}: ID = {}. Query String is: '{}'", module, eventId, queryString);
            ResultSet resultSet = statement.executeQuery();
            boolean firstExist = resultSet.first();
            if (firstExist) {
                result = buildEventFromResultSet(resultSet, connection);
            } else {
                LOG.info("{}: (LIGHT_WARN) For ID='{}' no event found in DB. Normal if it was deleted.", module, eventId);
            }
            //NOTE: Only one record should be found
            assert !resultSet.next();
            LOG.info("{}:  For ID ='{}' Event: '{}'", module, eventId, result);

        } catch (SQLException e) {
            throw new EtrException(String.format("%s - Error while select '%s' for id='%s'!", module, queryString, eventId), e);
        }
        return result;
    }

    private Event buildEventFromResultSet(ResultSet resultSet, Connection connection) throws SQLException {
        Event result = null;

        result = new Event();
        result.setId(resultSet.getLong("id"));
        result.setTitle(resultSet.getString("title"));

        // Set Category
        String categoryCode = resultSet.getString("category_code");
        if (categoryCode != null) {
            Event.Category category = this.eventCategoryCache.lookupCategory(categoryCode, connection);
            result.setCategory(category);
        }

        // Set Severity
        String severityCode = resultSet.getString("severity");
        if (severityCode != null) {
            Event.Severity severity = Event.Severity.valueOf(severityCode);
            if (severity != null) {
                result.setSeverity(severity);
            }
        }

        result.setSource(resultSet.getString("source"));
        result.setProcessId(resultSet.getString("process_id"));

        Timestamp createdAsSqlDate = resultSet.getTimestamp("created");
        if (createdAsSqlDate != null) {
            Date created = new Date(createdAsSqlDate.getTime());
            result.setCreated(created);
        }
        return result;
    }

}
