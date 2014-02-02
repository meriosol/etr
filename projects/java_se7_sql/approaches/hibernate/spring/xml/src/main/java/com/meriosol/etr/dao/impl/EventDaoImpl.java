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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service("EventDaoService")
public class EventDaoImpl implements EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDaoImpl.class);
    private static final Integer DEFAULT_MAX_EVENT_COUNT = 20; // normally client should query max 100

    @Autowired
    private SessionFactory sessionFactory;

    public EventDaoImpl() {
    }

    @Override
    public String getDaoName() {
        return "HibernateSpringXmlImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Hibernate Xml Spring implementation";
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
    public Event create(Event event) {
        final String module = "create";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }

        Session session = this.sessionFactory.openSession();
        session.save(event);
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
        Session session = this.sessionFactory.openSession();
        return (Event) session.get(Event.class, eventId);
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

        Session session = this.sessionFactory.openSession();
        // Choose your preferred way:
        List<Event> events = retrieveRecentEventsViaCriteria(maxEventCount, session);
        //events = retrieveRecentEventsViaQuery(maxEventCount, session);

        if (events != null) {
            for (Event event : events) {
                LOG.info(" oo event found: '{}'!", event);
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

        Session session = this.sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Event.class);
        Date startDateForExclusiveBetween = new Date(startDate.getTime() + 1);
        criteria.add(Restrictions.between("created", startDateForExclusiveBetween, endDate));
        criteria.addOrder(Order.desc("created"));
        criteria.setFirstResult(0);
        criteria.setMaxResults(DEFAULT_MAX_EVENT_COUNT);

        List<Event> events = (List<Event>) criteria.list();

        return events;
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

        Session session = this.sessionFactory.openSession();
        session.save(event);

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
        final String module = "deleteEvent";

        Session session = this.sessionFactory.openSession();
        String hql = "delete from Event where id= :eventId";
        session.createQuery(hql).setLong("eventId", eventId).executeUpdate();
    }

    //--------------------------------
    // Utils:

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
