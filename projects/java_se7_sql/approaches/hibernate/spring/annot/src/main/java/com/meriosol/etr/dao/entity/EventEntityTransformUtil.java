package com.meriosol.etr.dao.entity;

import com.meriosol.etr.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: find better entity mapping way..
 *
 * @author meriosol
 * @version 0.1
 * @since 29/01/14
 */
public class EventEntityTransformUtil {
    private EventEntityTransformUtil() {
    }

    /**
     * @param eventEntity
     * @return transformed <code>eventEntity</code>
     */
    public static Event transform(EventEntity eventEntity) {
        Event event = null;
        if (eventEntity != null) {
            event = new Event(EventCategoryEntityTransformUtil.transform(eventEntity.getCategory())
                    , eventEntity.getSeverity(), eventEntity.getSource(), eventEntity.getProcessId()
                    , eventEntity.getTitle(), eventEntity.getCreated(), eventEntity.getId());
        }
        return event;
    }

    /**
     * @param event
     * @return transformed <code>event</code>
     */
    public static EventEntity transform(Event event) {
        EventEntity eventEntity = null;
        if (event != null) {
            eventEntity = new EventEntity(EventCategoryEntityTransformUtil.transform(event.getCategory())
                    , event.getSeverity(), event.getSource(), event.getProcessId()
                    , event.getTitle(), event.getCreated(), event.getId());
        }
        return eventEntity;


    }

    /**
     * @param eventEntities
     * @return transformed <code>eventEntities</code>
     */
    public static List<Event> transform(List<EventEntity> eventEntities) {
        List<Event> events = null;
        if (eventEntities != null) {
            events = new ArrayList<>(eventEntities.size());
            for (EventEntity eventEntity : eventEntities) {
                events.add(transform(eventEntity));
            }
        }
        return events;
    }
}
