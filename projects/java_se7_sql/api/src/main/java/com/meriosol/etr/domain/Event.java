package com.meriosol.etr.domain;

import com.meriosol.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class Event implements Serializable {
    private Long id;
    private Category category;
    private Severity severity;
    private String source;
    private String processId;
    private String title;
    private Date created;

    /**
     * Event Category.
     */
    public static class Category implements Serializable {

        private String code;
        private String name;

        public Category() {
        }

        public Category(String code, String name) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Category category = (Category) o;
            return code.equals(category.code) && name.equals(category.name);
        }

        @Override
        public int hashCode() {
            int result = code.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "EventCategory{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public enum Severity {
        INFO, WARN, ERROR, FATAL
    }


    public Event() {
    }

    public Event(Category category, Severity severity, String source, String processId, String title, Date created, Long id) {
        this.category = category;
        this.severity = severity;
        this.source = source;
        this.processId = processId;
        this.title = title;
        this.created = created;
        this.id = id;
    }

    public Event(Category category, Severity severity, String source, String processId, String title) {
        this.category = category;
        this.severity = severity;
        this.source = source;
        this.processId = processId;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        String createdFormatted = null;
        if (this.created != null) {
            createdFormatted = DateUtil.formatDateWithDefaultFormat(created);
        }

        return "Event{" +
                "id=" + id +
                ", category=" + category +
                ", severity=" + severity +
                ", source='" + source + '\'' +
                ", processId='" + processId + '\'' +
                ", title='" + title + '\'' +
                ", created='" + createdFormatted +
                "'}";
    }
}
