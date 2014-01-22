package com.meriosol.etr.dao;

import com.meriosol.etr.dao.impl.EventDaoJdbcImpl;

/**
 * NOTE: This factory singleton is expected to present in each DAO approach.
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class EventDaoFactory {
    private static EventDaoFactory eventDaoFactory = new EventDaoFactory();

    public static EventDaoFactory getInstance() {
        return eventDaoFactory;
    }

    private EventDaoFactory() {
    }

    /**
     * @return EventDao impl
     */
    public EventDao loadEventDao() {
        return new EventDaoJdbcImpl();
    }

}
