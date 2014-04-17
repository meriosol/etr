package com.meriosol.etr.domain;

import java.util.Properties;

/**
 * EventInfo state POJO.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventInfo extends Info {
    private interface FieldNames {
        String TITLE = "title";
    }

    private String title;
    private EventCategoryInfo eventCategory;

    public EventInfo(Properties properties) {
        super(properties);
    }

    public EventInfo() {
        super();
    }

    @Override
    public String getName() {
        return "Event";
    }

    public String getTitle() {
        if (title != null) {
            return title;
        } else {
            return getProperties().getProperty(FieldNames.TITLE);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventCategory(EventCategoryInfo eventCategory) {
        this.eventCategory = eventCategory;
    }

    public EventCategoryInfo getEventCategory() {
        return eventCategory;
    }

    @Override
    public String toString() {
        return super.toString() + "_EventInfo{" +
                "title='" + getTitle() + '\'' +
                ", eventCategory=" + eventCategory +
                '}';
    }
}
