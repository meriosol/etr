package com.meriosol.etr.xml.sax.handling.domain;

import org.xml.sax.Attributes;

import java.util.Properties;

/**
 * EventInfo Category state POJO.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventCategoryInfo extends Info {
    private interface FieldNames {
        String NAME = "name";
    }

    private String categoryName;

    public EventCategoryInfo(Attributes attributes, Properties properties) {
        super(attributes, properties);
    }

    public EventCategoryInfo(Attributes attributes) {
        super(attributes);
    }

    public EventCategoryInfo(Properties properties) {
        super(properties);
    }

    public EventCategoryInfo() {
        super();
    }

    @Override
    public String getName() {
        return "EventCategory";
    }


    public String getCategoryName() {
        if (categoryName != null) {
            return categoryName;
        } else {
            return getProperties().getProperty(FieldNames.NAME);
        }
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return super.toString() + "_EventCategoryInfo{" +
                "name='" + getCategoryName() + '\'' +
                '}';
    }
}
