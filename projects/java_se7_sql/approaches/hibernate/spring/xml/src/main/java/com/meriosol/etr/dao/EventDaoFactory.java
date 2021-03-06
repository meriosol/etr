package com.meriosol.etr.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * NOTE: This factory singleton is expected to present in each DAO approach.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class EventDaoFactory {
    private static final EventDaoFactory eventDaoFactory = new EventDaoFactory();

    public static EventDaoFactory getInstance() {
        return eventDaoFactory;
    }

    private EventDaoFactory() {
    }

    /**
     * @return EventDao impl
     */
    public EventDao loadEventDao() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/meriosol/etr/dao/applicationContext.xml");
        return (EventDao) context.getBean("EventDaoService");
    }

}
