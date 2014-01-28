package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hibernate (XML) impl for Event DAO.
 *
 * @author meriosol
 * @version 0.1
 * @since 22/01/14
 */
public class EventDaoImpl implements EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDaoImpl.class);
    private static final Integer DEFAULT_MAX_EVENT_COUNT = 10000; // normally client should query max 100
    private SessionFactory sessionFactory;

    public EventDaoImpl() {
        initSessionFactory();
    }

    @Override
    public String getDaoName() {
        return "HibernateXmlImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Hibernate Xml implementation";
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

        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(event);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && !transaction.wasRolledBack()) {
                transaction.rollback();
            }
            LOG.error("Error while inserting event!", e);
        } finally {
            if (session != null) {
                session.close();
            }
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
        Event event = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            event = (Event) session.get(Event.class, eventId);

            // Longer way:
//            List<Event> events = session.createQuery("from Event where id = :idForRetrieve").setLong("idForRetrieve", eventId).list();
//            if (events != null) {
//                assert events.size() <= 1;
//                if (events.size() == 1) {
//                    event = events.get(0);
//                }
//            }

            // NOTE: if not to set lazy to false in hib mapper:
            //         <many-to-one name="category" class="Event$Category" column="category_code" cascade="all" not-null="false" lazy="false"/>
            // SLF4J: Failed toString() invocation on an object of type [com.meriosol.etr.domain.Event]
            // org.hibernate.LazyInitializationException: could not initialize proxy - no Session
            // To get rid of this temp hack(logger calls to event.toString() internally):
            LOG.debug("Event loaded: {}", event);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && !transaction.wasRolledBack()) {
                transaction.rollback();
            }
            LOG.error("Error while selecting event with id='{}'!", eventId, e);
        } finally {
            if (session != null) {
                session.close();
            }
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
    public List<Event> retrieveRecentEvents(Integer maxEventCount) {
        final String module = "retrieveRecentEvents";
        if (maxEventCount == null) {
            maxEventCount = DEFAULT_MAX_EVENT_COUNT;
        } else if (maxEventCount <= 0) {
            throw new IllegalArgumentException(module + " - maxEventCount should be > 0!");
        }

        Session session = null;
        Transaction transaction = null;
        List<Event> events = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Choose your preferred way:
            events = retrieveRecentEventsViaCriteria(maxEventCount, session);
            //events = retrieveRecentEventsViaQuery(maxEventCount, session);

            if (events != null) {
                for (Event event : events) {
                    LOG.info(" oo event found: '{}'!", event);
                }
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && !transaction.wasRolledBack()) {
                transaction.rollback();
            }
            LOG.error("Error while selecting events with maxEventCount='{}'!", maxEventCount, e);
        } finally {
            if (session != null) {
                session.close();
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
        Session session = null;
        Transaction transaction = null;
        List<Event> events = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(Event.class);
            Date startDateForExclusiveBetween = new Date(startDate.getTime() + 1);
            criteria.add(Restrictions.between("created", startDateForExclusiveBetween, endDate));
            criteria.addOrder(Order.desc("created"));
            criteria.setFirstResult(0);
            criteria.setMaxResults(DEFAULT_MAX_EVENT_COUNT);

            events = (List<Event>) criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && !transaction.wasRolledBack()) {
                transaction.rollback();
            }
            LOG.error("Error while selecting events with startDate='{}' and endDate='{}'!", startDate, endDate, e);
        } finally {
            if (session != null) {
                session.close();
            }
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

        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(event);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && !transaction.wasRolledBack()) {
                transaction.rollback();
            }
            LOG.error("Error while updating event with id='{}'!", event.getId(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return event;
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

        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            String hql = "delete from Event where id= :eventId";
            session.createQuery(hql).setLong("eventId", eventId).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && !transaction.wasRolledBack()) {
                transaction.rollback();
            }
            LOG.error("Error while deleting event with id='{}'!", eventId, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    //--------------------------------
    // Utils:
    private void initSessionFactory() {
        // A SessionFactory is set up once for an application
        // See http://www.javabeat.net/session-factory-hibernate-4/
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.sessionFactory != null) {
            try {
                this.sessionFactory.close();
            } catch (HibernateException e) {
                LOG.error("Error while closing sessionFactory!", e);
            }
        }
    }

    /**
     * @param maxEventCount
     * @param session
     * @return events
     */
    private List<Event> retrieveRecentEventsViaCriteria(Integer maxEventCount, Session session) {
        List<Event> events;
        Criteria criteria = session.createCriteria(Event.class);
        criteria.addOrder(Order.desc("created"));
        criteria.setFirstResult(0);
        criteria.setMaxResults(maxEventCount);

        events = (List<Event>) criteria.list();
        return events;
    }

    /**
     * @param maxEventCount
     * @param session
     * @return events
     */
    private List<Event> retrieveRecentEventsViaQuery(Integer maxEventCount, Session session) {
        List<Event> events = session.createQuery("from Event order by created desc").list();
        List<Event> eventsPaginated = new ArrayList<>();
        if (events != null) {
            if (maxEventCount > events.size()) {
                maxEventCount = events.size();
            }

            for (int i = 0; i < maxEventCount; i++) {
                eventsPaginated.add(events.get(i));
            }
        }
        return eventsPaginated;
    }
}
