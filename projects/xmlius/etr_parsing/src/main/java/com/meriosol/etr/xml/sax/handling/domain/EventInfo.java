package com.meriosol.etr.xml.sax.handling.domain;

import org.xml.sax.Attributes;

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

    public EventInfo(Attributes attributes, Properties properties) {
        super(attributes, properties);
    }

    public EventInfo(Attributes attributes) {
        super(attributes);
    }

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

    @Override
    public String toString() {
        return "EventInfo{" +
                "title='" + title + '\'' +
                ", eventCategory=" + eventCategory +
                '}';
    }
}
