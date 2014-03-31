package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

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
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;
    private EventCategoryCache eventCategoryCache;
    // NOTE: both low level JdbcTemplate and upper level NamedParameterJdbcTemplate are demonstrated for approach diversity.

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    interface DmlCommands {
        String BASE_EVENT_COLUMNS = "title, category_code, severity , source , process_id, created";

        String BASE_SELECT = "select id, category_code, title, severity , source , process_id, created " +
                "from events ";

        String ORDERBY_CREATED_DESC = "order by created desc";
        String INSERT = "insert into events (" + BASE_EVENT_COLUMNS + ") values (?,?,?,?,?,?)";
        String INSERT_WITH_NAMED_PARAMS = "insert into events (" + BASE_EVENT_COLUMNS + ") values (:title, :category.code, :severity, :source, :processId, :created)";
        String RETRIEVE_EVENT = BASE_SELECT + " where id = :id";
        String RETRIEVE_RECENT_EVENTS = BASE_SELECT + " " + ORDERBY_CREATED_DESC;
        String RETRIEVE_EVENTS_FOR_PERIOD = BASE_SELECT + " where created between :startDate and :endDate " + ORDERBY_CREATED_DESC;
        String UPDATE = "update events set title = :title, category_code = :category_code, severity = :severity ,"
                + " source = :source , process_id = :process_id where id = :id";
        String DELETE = "delete from events where id = :id";
    }


    public EventDaoImpl() {
        this.eventCategoryCache = EventCategoryCache.getInstance();
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
    public Event create(final Event event) {
        final String module = "create";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(DmlCommands.INSERT, new String[]{"id"});
                        ps.setString(1, event.getTitle());

                        Event.Category category = event.getCategory();
                        if (category != null) {
                            ps.setString(2, category.getCode());
                        }

                        Event.Severity severity = event.getSeverity();
                        if (severity != null) {
                            ps.setString(3, severity.name());
                        }

                        ps.setString(4, event.getSource());
                        ps.setString(5, event.getProcessId());
                        Date created = event.getCreated();
                        if (created != null) {
                            ps.setTimestamp(6, new Timestamp(created.getTime()));
                        }
                        return ps;
                    }
                },
                keyHolder
        );
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            event.setId(generatedKey.longValue());
        }
        // TODO: what about created field (it has default value on DB)?

        // TODO: won't work for some fields, fix it!
//        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(event);
//        int howManyRowsInserted = this.namedParameterJdbcTemplate.update(DmlCommands.INSERT, namedParameters);
//        assert howManyRowsInserted == 1;
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
        Map<String, Long> namedParameters = Collections.singletonMap("id", eventId);
        try {
            return this.namedParameterJdbcTemplate.queryForObject(DmlCommands.RETRIEVE_EVENT, namedParameters
                    , new EventMapper());
        } catch (EmptyResultDataAccessException e) {
            LOG.info("No value returned for Event with id '" + eventId + "'. For some tests(e.g. create/delete) it can be normal.", e);
            return null;
        }
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

        // TODO: Pagination is not ANSI SQL for now, vendor specific approaches are needed (offset/limit in mysql/gsql, rownum in oracle).
        // See discussion e.g. here - http://stackoverflow.com/questions/2771439/jdbc-pagination
        // For now let's use primitive way (not for production use).
        List<Event> events = new ArrayList<>();
        List<Event> eventsFromDB = this.namedParameterJdbcTemplate.query(DmlCommands.RETRIEVE_RECENT_EVENTS, new EventMapper());
        if (eventsFromDB != null && eventsFromDB.size() > 0) {
            if (maxEventCount > eventsFromDB.size()) {
                maxEventCount = eventsFromDB.size();
            }
            for (int i = 0; i < maxEventCount; i++) {
                events.add(eventsFromDB.get(i));
            }
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
    @Transactional
    public List<Event> retrieveEventsForPeriod(Date startDate, Date endDate) {
        final String module = "retrieveEventsForPeriod";
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
            startDate = new Date(System.currentTimeMillis() - 100 * 60 * 60 * 24 * 365 * 10);
        }
        if (endDate == null) {
            endDate = new Date();
        }
        Map<String, Object> namedParameters = new HashMap<>(2);
        namedParameters.put("startDate", startDate);
        namedParameters.put("endDate", endDate);
        return this.namedParameterJdbcTemplate.query(DmlCommands.RETRIEVE_EVENTS_FOR_PERIOD, namedParameters, new EventMapper());
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

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("title", event.getTitle());

        Event.Category category = event.getCategory();
        if (category != null) {
            namedParameters.addValue("category_code", category.getCode());
        }

        Event.Severity severity = event.getSeverity();
        if (severity != null) {
            namedParameters.addValue("severity", severity.name());
        }

        namedParameters.addValue("source", event.getSource());
        namedParameters.addValue("process_id", event.getProcessId());
        namedParameters.addValue("id", event.getId());

        int howManyRowsUpdated = this.namedParameterJdbcTemplate.update(DmlCommands.UPDATE, namedParameters);
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
        Map<String, Long> namedParameters = Collections.singletonMap("id", eventId);
        int howManyRowsUpdated = this.namedParameterJdbcTemplate.update(DmlCommands.DELETE, namedParameters);
        assert howManyRowsUpdated == 1;
    }

    //------------------------------------
    // Supplement classes/methods:

    /**
     * Event JDBC-POJO mapper.
     */
    private final class EventMapper implements RowMapper<Event> {

        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = new Event();

            event.setId(rs.getLong("id"));
            String categoryCode = rs.getString("category_code");
            if (categoryCode != null) {
                // TODO: this way may be not the most optimal..consider 2nd level cache (ehcache).
                Event.Category category = eventCategoryCache.lookupCategory(categoryCode, jdbcTemplate);
                event.setCategory(category);
            }

            String severityCode = rs.getString("severity");
            if (severityCode != null) {
                event.setSeverity(Event.Severity.valueOf(severityCode));
            }

            event.setTitle(rs.getString("title"));
            event.setSource(rs.getString("id"));
            event.setProcessId(rs.getString("id"));
            Timestamp timestampCreated = rs.getTimestamp("created");
            if (timestampCreated != null) {
                event.setCreated(new Date(timestampCreated.getTime()));
            }
            return event;
        }


    }
}
