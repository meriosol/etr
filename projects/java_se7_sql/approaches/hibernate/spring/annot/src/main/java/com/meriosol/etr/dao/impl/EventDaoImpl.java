package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.dao.entity.EventEntity;
import com.meriosol.etr.dao.entity.EventEntityTransformUtil;
import com.meriosol.etr.domain.Event;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private static final Integer DEFAULT_MAX_EVENT_COUNT = 10000; // normally client should query max 100

    @Autowired
    private SessionFactory sessionFactory;

    public EventDaoImpl() {
    }

    @Override
    public String getDaoName() {
        return "HibernateSpringAnnotImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Hibernate Annotations Spring implementation";
    }

    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     * Output invariant1: id should appear in returned object event.<br>
     * TODO: weird behavior: some code inside hib removes category.name (e.g. for MESSAGE).
     * I had to set it back. Reason can be in cascade binding for many-to-one for category..
     *
     * @param event
     * @return Created event.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Event create(Event event) {
        final String module = "create";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }

        Session session = this.sessionFactory.getCurrentSession();
        // TODO: Well, this transform business looks sick..
        EventEntity eventEntity = EventEntityTransformUtil.transform(event);
        session.save(eventEntity);
        return EventEntityTransformUtil.transform(eventEntity);
    }

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Event retrieveEvent(Long eventId) {
        Session session = this.sessionFactory.getCurrentSession();
        EventEntity eventEntity = (EventEntity) session.get(EventEntity.class, eventId);
        return EventEntityTransformUtil.transform(eventEntity);
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Event> retrieveRecentEvents(Integer maxEventCount) {
        final String module = "retrieveRecentEvents";
        if (maxEventCount == null) {
            maxEventCount = DEFAULT_MAX_EVENT_COUNT;
        } else if (maxEventCount <= 0) {
            throw new IllegalArgumentException(module + " - maxEventCount should be > 0!");
        }

        Session session = this.sessionFactory.getCurrentSession();
        // Choose your preferred way:
        List<EventEntity> eventEntities = retrieveRecentEventsViaCriteria(maxEventCount, session);
        //List<EventEntity> eventEntities  = retrieveRecentEventsViaQuery(maxEventCount, session);
        if (eventEntities != null) {
            LOG.info(" oo ===============V eventEntities ('{}' found):", eventEntities.size());
            for (EventEntity eventEntity : eventEntities) {
                LOG.info(" oo eventEntity: '{}'!", eventEntity);
            }
        }

        List<Event> events = EventEntityTransformUtil.transform(eventEntities);

        if (events != null) {
            LOG.info(" oo ===============V events ('{}' found):", events.size());
            for (Event event : events) {
                LOG.info(" oo event: '{}'!", event);
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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

        Session session = this.sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(EventEntity.class);
        Date startDateForExclusiveBetween = new Date(startDate.getTime() + 1);
        criteria.add(Restrictions.between("created", startDateForExclusiveBetween, endDate));
        criteria.addOrder(Order.desc("created"));
        criteria.setFirstResult(0);
        criteria.setMaxResults(DEFAULT_MAX_EVENT_COUNT);

        List<Event> events = EventEntityTransformUtil.transform((List<EventEntity>) criteria.list());

        return events;
    }

    /**
     * @param event
     * @return Updated event(normally exactly the same object).
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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

        Session session = this.sessionFactory.getCurrentSession();

        EventEntity eventEntity = EventEntityTransformUtil.transform(event);
        session.update(eventEntity);
        return EventEntityTransformUtil.transform(eventEntity);
    }

    /**
     * Deletes event.
     *
     * @param event
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteEvent(Long eventId) {
        final String module = "deleteEvent";

        Session session = this.sessionFactory.getCurrentSession();
        String hql = "delete from EventEntity where id= :eventId";
        session.createQuery(hql).setLong("eventId", eventId).executeUpdate();
    }

    //--------------------------------
    // Utils:

    /**
     * @param maxEventCount
     * @param session
     * @return events
     */
    private List<EventEntity> retrieveRecentEventsViaCriteria(Integer maxEventCount, Session session) {
        List<EventEntity> events;
        Criteria criteria = session.createCriteria(EventEntity.class);
        criteria.addOrder(Order.desc("created"));
        criteria.setFirstResult(0);
        criteria.setMaxResults(maxEventCount);

        events = (List<EventEntity>) criteria.list();
        return events;
    }

    /**
     * @param maxEventCount
     * @param session
     * @return events
     */
    private List<EventEntity> retrieveRecentEventsViaQuery(Integer maxEventCount, Session session) {
        List<EventEntity> events = session.createQuery("from EventEntity order by created desc").list();
        List<EventEntity> eventsPaginated = new ArrayList<>();
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
