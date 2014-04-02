package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.dao.entity.EventEntity;
import com.meriosol.etr.dao.entity.EventEntityTransformUtil;
import com.meriosol.etr.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
    public static final int TIME = 100 * 60 * 60 * 24 * 365 * 10;

    @Autowired
    private EntityManager entityManager;

    public EventDaoImpl() {
    }

    @Override
    public String getDaoName() {
        return "HibernateSpringJpaAnnotImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Hibernate Annotations Spring implementation via JPA";
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

        // TODO: Well, this transform business looks sick..
        EventEntity eventEntity = EventEntityTransformUtil.transform(event);
        EventEntity mergedEventEntity = this.entityManager.merge(eventEntity);
        this.entityManager.persist(mergedEventEntity);
        return EventEntityTransformUtil.transform(mergedEventEntity);
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
        // Ways:
        // 1. JPA 1 style:
        // EventEntity eventEntity = this.entityManager.createQuery("from EventEntity where id = " + eventId, EventEntity.class).getSingleResult();
        // 2. Criteria style (for type safe query maniacs):
        // TODO: use metamodel when ready:
//        Metamodel metamodel = this.entityManager.getMetamodel();
//        EntityType<EventEntity> eventEntityModel = metamodel.entity(EventEntity.class);
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> cq = cb.createQuery(EventEntity.class);
        Root<EventEntity> eventEntityRoot = cq.from(EventEntity.class);
        cq.select(eventEntityRoot);
        String pkFieldName = "id";
        String idParamName = "id";
        cq.where(cb.equal(eventEntityRoot.<Long>get(pkFieldName)
                , cb.parameter(Long.class, idParamName)));

        Query query = this.entityManager.createQuery(cq);
        query.setParameter(idParamName, eventId);

        EventEntity eventEntity = null;
        try {
            eventEntity = (EventEntity) query.getSingleResult();
        } catch (NoResultException e) {
            LOG.info("No entity found for eventId='{}'. In some cases it can be normal", eventId);
        }

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

        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // TODO: use metamodel when ready:
//        Metamodel metamodel = this.entityManager.getMetamodel();
//        EntityType<EventEntity> eventEntityModel = metamodel.entity(EventEntity.class);

        CriteriaQuery<EventEntity> cq = cb.createQuery(EventEntity.class);
        Root<EventEntity> eventEntityRoot = cq.from(EventEntity.class);
        cq.select(eventEntityRoot);
        cq.orderBy(cb.desc(eventEntityRoot.get("created"))); // TODO: use metamodel
        TypedQuery<EventEntity> query = this.entityManager.createQuery(cq);
        query.setFirstResult(0).setMaxResults(maxEventCount);

        List<EventEntity> eventEntities = query.getResultList();

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
            startDate = new Date(System.currentTimeMillis() - TIME);
        }
        if (endDate == null) {
            endDate = new Date();
        }

        Date startDateForExclusiveBetween = new Date(startDate.getTime() + 1);


        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // TODO: use metamodel when ready:
//        Metamodel metamodel = this.entityManager.getMetamodel();
//        EntityType<EventEntity> eventEntityModel = metamodel.entity(EventEntity.class);

        CriteriaQuery<EventEntity> cq = cb.createQuery(EventEntity.class);
        Root<EventEntity> eventEntityRoot = cq.from(EventEntity.class);
        Path<Date> createdFieldPath = eventEntityRoot.get("created");
        cq.select(eventEntityRoot);
        cq.orderBy(cb.desc(createdFieldPath)); // TODO: use metamodel

        String startDateParamName = "startDate";
        String endDateParamName = "endDate";

        cq.where(cb.between(createdFieldPath
                , cb.parameter(Date.class, startDateParamName), cb.parameter(Date.class, endDateParamName)));

        TypedQuery<EventEntity> query = this.entityManager.createQuery(cq);
        query.setParameter(startDateParamName, startDateForExclusiveBetween);
        query.setParameter(endDateParamName, endDate);
        query.setFirstResult(0).setMaxResults(DEFAULT_MAX_EVENT_COUNT);

        List<EventEntity> eventEntities = query.getResultList();

        List<Event> events = EventEntityTransformUtil.transform(eventEntities);

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

        EventEntity eventEntity = EventEntityTransformUtil.transform(event);
        EventEntity mergedEventEntity = this.entityManager.merge(eventEntity);
        this.entityManager.persist(mergedEventEntity);
        return EventEntityTransformUtil.transform(mergedEventEntity);
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

        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaDelete delete = cb.createCriteriaDelete(EventEntity.class);
        String pkFieldName = "id";
        String idParamName = "id";

        Root rootEventEntity = delete.from(EventEntity.class);

        delete.where(cb.equal(rootEventEntity.<Long>get(pkFieldName)
                , cb.parameter(Long.class, idParamName)));

        Query query = this.entityManager.createQuery(delete);
        query.setParameter(idParamName, eventId);

        Integer deletedEntityCount = query.executeUpdate();
        LOG.info(" oo eventEntity with eventId '{}' deleted. deletedEntityCount = '{}'.", eventId, deletedEntityCount);
    }

    //--------------------------------
    // Utils:

}
