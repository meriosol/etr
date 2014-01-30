package com.meriosol.etr.dao.entity;

import com.meriosol.etr.domain.Event;

/**
 * TODO: find better entity mapping way..
 *
 * @author meriosol
 * @version 0.1
 * @since 29/01/14
 */
public class EventCategoryEntityTransformUtil {
    private EventCategoryEntityTransformUtil() {
    }

    /**
     * @param eventCategoryEntity
     * @return transformed <code>eventCategoryEntity</code>
     */
    public static Event.Category transform(EventCategoryEntity eventCategoryEntity) {
        Event.Category category = null;
        if (eventCategoryEntity != null) {
            category = new Event.Category(eventCategoryEntity.getCode(), eventCategoryEntity.getName());
        }
        return category;
    }

    /**
     * @param eventCategory
     * @return transformed <code>eventCategory</code>
     */
    public static EventCategoryEntity transform(Event.Category eventCategory) {
        EventCategoryEntity categoryEntity = null;
        if (eventCategory != null) {
            categoryEntity = new EventCategoryEntity(eventCategory.getCode(), eventCategory.getName());
        }
        return categoryEntity;
    }
}
