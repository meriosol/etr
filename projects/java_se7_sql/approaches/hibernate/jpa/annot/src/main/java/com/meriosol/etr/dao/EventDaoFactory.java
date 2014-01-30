package com.meriosol.etr.dao;

import com.meriosol.etr.dao.impl.EventDaoImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * NOTE: This factory singleton is expected to present in each DAO approach.
 *
 * @author meriosol
 * @version 0.1
 * @since 30/01/14
 */
public class EventDaoFactory {
    private static EventDaoFactory eventDaoFactory = new EventDaoFactory();
    private EntityManagerFactory entityManagerFactory;
    private static final String PERSISTENT_UNIT_NAME = "com.meriosol.etr.dao.jpa";

    public static EventDaoFactory getInstance() {
        return eventDaoFactory;
    }

    private EventDaoFactory() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENT_UNIT_NAME);
    }

    /**
     * @return EventDao impl
     */
    public EventDao loadEventDao() {
        return new EventDaoImpl(this.entityManagerFactory);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {
            this.entityManagerFactory.close();
        }
    }
}
