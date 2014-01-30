package com.meriosol.etr.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ~100% Event.Category class, but has hibernate annotations.
 * @author meriosol
 * @version 0.1
 * @since 29/01/14
 */
@Entity
@Table(name = "event_categories")
public class EventCategoryEntity {
    @Id
    @Column(name = "code", insertable = true, updatable = false)
    private String code;

    @Column(name = "name", insertable = true, updatable = true)
    private String name;

    public EventCategoryEntity() {
    }

    public EventCategoryEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EventCategoryEntity{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
