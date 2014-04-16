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
        String CODE = "code";
        String NAME = "name";
    }

    private String code;
    private String categoryName;

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

    public String getCode() {
        if (code != null) {
            return code;
        } else {
            return getProperties().getProperty(FieldNames.CODE);
        }
    }

    public void setCode(String code) {
        this.code = code;
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
        return "EventCategoryInfo{" +
                "code='" + getCode() + '\'' +
                ", categoryName='" + getCategoryName() + '\'' +
                '}';
    }
}
