package com.meriosol.etr.dao.entity;

import com.meriosol.etr.domain.Event;
import com.meriosol.util.DateUtil;

import javax.persistence.*;
import java.util.Date;

/**
 * ~100% Event class, but has hibernate annotations.
 *
 * @author meriosol
 * @version 0.1
 * @since 29/01/14
 */
@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @Column(name = "id", insertable = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: explore details of CascadeType..
//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_code", nullable = true, insertable = true, updatable = true)
    private EventCategoryEntity category;

    /**
     * OK, let's try to use enum for now..
     */
    @Column(name = "severity", insertable = true, nullable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    private Event.Severity severity;

    @Column(name = "source", insertable = true, updatable = true, nullable = true, length = 100)
    private String source;

    @Column(name = "process_id", insertable = true, updatable = true, nullable = true, length = 100)
    private String processId;

    @Column(name = "title", insertable = true, updatable = true, nullable = true, length = 100)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", insertable = true, updatable = false, nullable = false)
    private Date created;


    public EventEntity() {
    }

    public EventEntity(EventCategoryEntity category, Event.Severity severity, String source, String processId
            , String title, Date created, Long id) {
        this.category = category;
        this.severity = severity;
        this.source = source;
        this.processId = processId;
        this.title = title;
        this.created = created;
        this.id = id;
    }

    public EventEntity(EventCategoryEntity category, Event.Severity severity, String source, String processId, String title) {
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

    public EventCategoryEntity getCategory() {
        return category;
    }

    public void setCategory(EventCategoryEntity category) {
        this.category = category;
    }

    public Event.Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Event.Severity severity) {
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
