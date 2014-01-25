package com.meriosol.etr.dao.mapper;

import com.meriosol.etr.domain.Event;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * NOTE: This interface resembles <code>EventDao</code>, but with tiny signature deviations.
 *
 * @author meriosol
 * @version 0.1
 * @since 24/01/14
 */
@CacheNamespace(implementation = org.mybatis.caches.ehcache.EhcacheCache.class)
public interface EventMapper {
    interface DmlCommands {
        String BASE_EVENT_COLUMNS = "title, category_code, severity , source , process_id, created";

        //TODO: Get back once basics start working
//        String BASE_SELECT = "select e.id as \"event_id\", e.title as \"event_title\", ec.code as \"category_code\", "
//                + "ec.name as \"category_name\", e.severity as \"event_severity\", e.source as \"event_source\", "
//                + "e.process_id as \"event_process_id\", e.created as \"event_created\" "
//                + "from events e left join event_categories ec on e.category_code = ec.code";

        String BASE_SELECT = "select id, category_code, title, severity , source , process_id, created " +
                "from events ";

        String ORDERBY_CREATED_DESC = "order by created desc";
        String INSERT = "insert into events (" + BASE_EVENT_COLUMNS + ") values (#{title}, #{category.code}, #{severity}, #{source}, #{processId}, #{created})";
        String RETRIEVE_EVENT = BASE_SELECT + " where id = #{id}";
        String RETRIEVE_RECENT_EVENTS = BASE_SELECT + " " + ORDERBY_CREATED_DESC;
        String RETRIEVE_EVENTS_FOR_PERIOD = BASE_SELECT + " where created between #{startDate} and #{endDate} " + ORDERBY_CREATED_DESC;
        String UPDATE = "update events set title = #{title}, category_code = #{category.code}, severity = #{severity} ,"
                + " source = #{source} , process_id = #{processId} where id = #{id}";
        String DELETE = "delete from events where id = #{id}";
    }

    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     *
     * @param event
     * @return How many rows got created. NOTE: both ID and title should be present in <code>event</code> object after insert.
     */
    @Insert(DmlCommands.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id", flushCache = true)
    int create(Event event);

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    @Select(DmlCommands.RETRIEVE_EVENT)
    @Options(useCache = true)
    @MapKey("id")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "category", column = "category_code", javaType = Event.Category.class
                    , one = @One(select = "com.meriosol.etr.dao.mapper.EventCategoryMapper.retrieveEventCategory")),
            @Result(property = "title", column = "title"),
            @Result(property = "severity", column = "severity"),
            @Result(property = "source", column = "source"),
            @Result(property = "processId", column = "process_id"),
            @Result(property = "created", column = "created")
    })
    public Event retrieveEvent(Long eventId);

    /**
     * Output invariant1: Sorting should be by field 'created' desc.<br>
     * Output invariant2:  should be List.size <= maxEventCount.<br>
     *
     * @param rowBounds pagination object.<br>
     *                  NOTE: If null provided, all events to be returned (CAUTION: dangerous case if DB is very large).
     * @return List of events.
     */
    @Select(DmlCommands.RETRIEVE_RECENT_EVENTS)
    @Options(useCache = true)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "category", column = "category_code", javaType = Event.Category.class
                    , one = @One(select = "com.meriosol.etr.dao.mapper.EventCategoryMapper.retrieveEventCategory")),
            @Result(property = "title", column = "title"),
            @Result(property = "severity", column = "severity"),
            @Result(property = "source", column = "source"),
            @Result(property = "processId", column = "process_id"),
            @Result(property = "created", column = "created")
    })
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
    @Select(DmlCommands.RETRIEVE_EVENTS_FOR_PERIOD)
    @Options(useCache = true)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "category", column = "category_code", javaType = Event.Category.class
                    , one = @One(select = "com.meriosol.etr.dao.mapper.EventCategoryMapper.retrieveEventCategory")),
            @Result(property = "title", column = "title"),
            @Result(property = "severity", column = "severity"),
            @Result(property = "source", column = "source"),
            @Result(property = "processId", column = "process_id"),
            @Result(property = "created", column = "created")
    })
    List<Event> retrieveEventsForPeriod(Map<String, Object> params);

    /**
     * @param event
     * @return how many rows were updated (normally should be 1).
     */
    @Update(DmlCommands.UPDATE)
    @Options(flushCache = true)
    int update(Event event);

    /**
     * Deletes event.
     *
     * @param eventId
     * @return how many rows were deleted (normally should be 1).
     */
    @Delete(DmlCommands.DELETE)
    @Options(flushCache = true)
    int delete(Long eventId);

}
