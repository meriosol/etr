package com.meriosol.etr.dao.impl;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD methods for event slice table(column family). This slice was created to support ordering by field 'created'.
 * Main events CF is basically hashmap(so no ordering) of events.<br>
 * NOTE: technically it's DAO for event slices.
 *
 * @author meriosol
 * @version 0.1
 * @since 09/02/14
 */
class EventByCreatedSliceCrud {
    //private static final Logger LOG = LoggerFactory.getLogger(EventByCreatedSliceCrud.class);
    private static EventByCreatedSliceCrud instance = new EventByCreatedSliceCrud();

    interface EventSliceEntityNames {
        String TABLE = "event_by_created_slices";

        interface Column {
            String YEAR = "year";
            String EVENT_ID = "event_id";
            String CREATED = "created";
        }
    }

    // TODO: (Session session, String keyspace) - passed in each method. Maybe to add them as fields(but then many objects will be subject to GC)?

    static EventByCreatedSliceCrud getInstance() {
        return instance;
    }

    private EventByCreatedSliceCrud() {
    }


    /**
     * @param createdTime
     * @param eventId
     * @param session
     * @param keyspace
     */
    void insertByCreatedSlice(Long createdTime, Long eventId, Session session, String keyspace) {
        DateTime created = new DateTime(createdTime);
        int year = created.getYear();

        Query insert = QueryBuilder.insertInto(keyspace, EventSliceEntityNames.TABLE)
                .value(EventSliceEntityNames.Column.YEAR, year)
                .value(EventSliceEntityNames.Column.CREATED, createdTime)
                .value(EventSliceEntityNames.Column.EVENT_ID, eventId)
                .setConsistencyLevel(ConsistencyLevel.ONE);

        ResultSet results = session.execute(insert);
    }

    List<Long> loadRecentEventIds(int limit, Session session, String keyspace) {
        // 1. Load slices
        int year1 = new DateTime().getYear();
        // TODO: for clarity more prev-s years to be checked too(well, dirty workaround anyway)..
        // TODO: use QueryBuilder
        int year2 = year1 - 1;

        String sliceSelector = "SELECT " + EventSliceEntityNames.Column.EVENT_ID + ", " + EventSliceEntityNames.Column.CREATED
                + " FROM " + keyspace + "." + EventSliceEntityNames.TABLE + " WHERE year in (?,?) ORDER BY created desc LIMIT ?;";
        PreparedStatement statement = session.prepare(sliceSelector);
        BoundStatement boundStatement = statement.bind(year1, year2, limit);
        ResultSet eventIdsRS = session.execute(boundStatement);
        return constructEventIdsFromRS(eventIdsRS);
    }

    List<Long> loadEventsForPeriod(Long timeFrom, Long timeTo, int limit, Session session, String keyspace) {
        int year1 = new DateTime().getYear();
        // TODO: for clarity more prev-s years to be checked too(well, dirty workaround anyway)..
        // TODO: use QueryBuilder
        int year2 = year1 - 1;
        // NOTE: for clarity prev-s years to be checked too..

        // 1. Load slices
        // Sample:
        //   SELECT year, created, event_id FROM event_by_created_slices
        //    WHERE year = 2014 AND created > 1391669022854001 AND created <= 1391669022854004 ORDER BY created DESC LIMIT 3;
        // TODO: use query builder when ready..

        String sliceSelector = "SELECT " + EventSliceEntityNames.Column.EVENT_ID + ", " + EventSliceEntityNames.Column.CREATED
                + " FROM " + keyspace + "." + EventSliceEntityNames.TABLE +
                " WHERE year IN (?,?) AND created >= ? AND created < ? ORDER BY created DESC LIMIT ?;";

        PreparedStatement statement = session.prepare(sliceSelector);
        BoundStatement boundStatement = statement.bind(year1, year2, timeFrom, timeTo, limit);

        ResultSet eventIds = session.execute(boundStatement);
        ResultSet eventIdsRS = session.execute(boundStatement);
        return constructEventIdsFromRS(eventIdsRS);
    }

    void deleteEventByCreatedSlice(Long eventId, Long createdTime, Session session, String keyspace) {
        final String module = "deleteEventByCreatedSlice";
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }
        if (createdTime == null) {
            throw new IllegalArgumentException(module + " - CreatedTime should not be null!");
        }

        DateTime created = new DateTime(createdTime);
        int year = created.getYear();

        // Prepare delete:
        String deleteDml = "DELETE FROM " + keyspace
                + "." + EventSliceEntityNames.TABLE + " WHERE year = ? AND created = ? AND  event_id = ?;";
        PreparedStatement statement = session.prepare(deleteDml);
        BoundStatement boundStatement = statement.bind(year, createdTime, eventId);

        // Destroy, eh..
        session.execute(boundStatement);
    }

    private static List<Long> constructEventIdsFromRS(ResultSet eventIdsRS) {
        List<Long> eventIds = null;
        if (eventIdsRS != null) {
            eventIds = new ArrayList<>();
            for (Row eventIdRow : eventIdsRS) {
                eventIds.add(eventIdRow.getLong(EventSliceEntityNames.Column.EVENT_ID));
            }
        }
        return eventIds;
    }


}
