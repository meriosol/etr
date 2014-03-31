package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;
import com.meriosol.exception.EtrException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * JDBC impl for Event DAO.
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class EventDaoImpl implements EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDaoImpl.class);

    private static final Integer DEFAULT_MAX_EVENT_COUNT = 20; // normally client should query max 100
    private SolrServerFactory solrServerFactory;
    private EventCategoryCache eventCategoryCache;

    interface EventEntityNames {
        String TABLE = "events";

        interface Column {
            String CREATED = "created";
        }
    }


    public EventDaoImpl() {
        this.solrServerFactory = SolrServerFactory.getInstance();
        this.eventCategoryCache = EventCategoryCache.getInstance();
    }

    @Override
    public String getDaoName() {
        return "SolrDirectImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Solr/Direct implementaion";
    }

    /**
     * Input invariant1: event.id should be null in input event. It's assumed backend deals with ID generation.<br>
     * Output invariant1: id should appear in returned object event.<br>
     *
     * @param event
     * @return Created event.
     */
    @Override
    public Event create(Event event) {
        final String module = "create";
        if (event == null) {
            throw new IllegalArgumentException(module + " - Event should not be null!");
        }
        if (event.getTitle() == null) {
            throw new IllegalArgumentException(module + " - Event Title should not be null!");
        }
        SolrServer solrServer = findEventSolrServer();
        if (solrServer == null) {
            throw new EtrException(module + " - solrServer should not be null!");
        }

        Event result = event; // TODO: ideally sh be cloned..

        // ID:
        Long id = event.getId();
        if (id == null) {
            id = IdGenerator.generateNewId();
        }

        // Category:
        Event.Category category = event.getCategory();
        String categoryCode = null;
        if (category != null) {
            categoryCode = category.getCode();
        }

        // Severity:
        String severityCode = null;
        Event.Severity eventSeverity = event.getSeverity();
        if (eventSeverity != null) {
            severityCode = eventSeverity.name();
        }

        // Created:
        Date created = event.getCreated();
        if (created == null) {
            created = new Date();
        }

        SolrInputDocument solrDocument = new SolrInputDocument();

        solrDocument.addField("id", id);
        solrDocument.addField("category_code", categoryCode);
        solrDocument.addField("title", event.getTitle());
        solrDocument.addField("severity", severityCode);
        solrDocument.addField("source", event.getSource());
        solrDocument.addField("process_id", event.getProcessId());
        solrDocument.addField(EventEntityNames.Column.CREATED, created);

        try {
            UpdateResponse response = solrServer.add(solrDocument);
            solrServer.commit();

            // Finally update resulting event POJO:
            result.setId(id);
            result.setCreated(created);
        } catch (SolrServerException e) {
            throw new EtrException(e);
        } catch (IOException e) {
            throw new EtrException(e);
        }

        return result;
    }

    /**
     * Output invariant1: should be Event.id = eventId.<br>
     *
     * @param eventId
     * @return Event.
     */
    @Override
    public Event retrieveEvent(Long eventId) {
        final String module = "retrieveEvent";
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }
        SolrServer solrServer = findEventSolrServer();
        if (solrServer == null) {
            throw new EtrException(module + " - solrServer should not be null!");
        }

        Object responseId = null;
        String queryString = "id:" + eventId;
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", queryString);

        Event event = null;
        QueryResponse response = null;
        try {
            response = solrServer.query(solrQuery);
            if (response != null) {
                SolrDocumentList results = response.getResults();
                if (results != null && results.size() > 0) {
                    if (results.size() > 1) {
                        throw new EtrException(String.format("Too many events found for ID='%s'!", eventId));
                    }
                    SolrDocument solrDocument = results.get(0);
                    event = buildEventFromResultSet(solrDocument);

                }
            }

        } catch (SolrServerException e) {
            throw new EtrException(e);
        }

        return event;
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
    public List<Event> retrieveRecentEvents(Integer maxEventCount) {
        final String module = "retrieveRecentEvents";

        SolrServer solrServer = findEventSolrServer();
        if (solrServer == null) {
            throw new EtrException(module + " - solrServer should not be null!");
        }

        String queryString = "*:*";
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", queryString);

        // Sort by date desc:
        solrQuery.addSort(EventEntityNames.Column.CREATED, SolrQuery.ORDER.desc);

        // Pagination:
        solrQuery.setStart(0);
        solrQuery.setRows(maxEventCount);

        return loadEvents(solrQuery, solrServer);
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
    public List<Event> retrieveEventsForPeriod(Date startDate, Date endDate) {
        final String module = "retrieveEventsForPeriod";
        SolrServer solrServer = findEventSolrServer();
        if (solrServer == null) {
            throw new EtrException(module + " - solrServer should not be null!");
        }
        if (startDate != null && endDate != null) {
            if (startDate.getTime() >= endDate.getTime()) {
                throw new IllegalArgumentException("startDate should be < endDate!");
            }
        }

        String startDateFormatted = DateTimeConditionValuePreparer.prepareDateForCondition(startDate);
        String endDateDateFormatted = DateTimeConditionValuePreparer.prepareDateForCondition(endDate);

        String queryString = String.format("+%s:{%s TO %s]", EventEntityNames.Column.CREATED, startDateFormatted, endDateDateFormatted);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", queryString);

        // Sort by date desc:
        solrQuery.addSort(EventEntityNames.Column.CREATED, SolrQuery.ORDER.desc);

        // Pagination:
        solrQuery.setStart(0);
        solrQuery.setRows(DEFAULT_MAX_EVENT_COUNT);

        return loadEvents(solrQuery, solrServer);
    }

    /**
     * @param event
     * @return Updated event(normally exactly the same object).
     */
    @Override
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
        SolrServer solrServer = findEventSolrServer();
        if (solrServer == null) {
            throw new EtrException(module + " - solrServer should not be null!");
        }

        Event result = event; // TODO: ideally sh be cloned..


        // Category:
        Event.Category category = event.getCategory();
        String categoryCode = null;
        if (category != null) {
            categoryCode = category.getCode();
        }

        // Severity:
        String severityCode = null;
        Event.Severity eventSeverity = event.getSeverity();
        if (eventSeverity != null) {
            severityCode = eventSeverity.name();
        }

        SolrInputDocument solrDocument = new SolrInputDocument();
        solrDocument.addField("id", event.getId());

        addFieldForPartialUpdate(solrDocument, "title", event.getTitle());
        addFieldForPartialUpdate(solrDocument, "category_code", categoryCode);
        addFieldForPartialUpdate(solrDocument, "severity", severityCode);
        addFieldForPartialUpdate(solrDocument, "source", event.getSource());
        addFieldForPartialUpdate(solrDocument, "process_id", event.getProcessId());

        try {
            UpdateResponse response = solrServer.add(solrDocument);
            solrServer.commit();
        } catch (SolrServerException e) {
            throw new EtrException(e);
        } catch (IOException e) {
            throw new EtrException(e);
        }

        return result;
    }


    /**
     * Deletes event.
     *
     * @param event
     */
    @Override
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
    public void deleteEvent(Long eventId) {
        final String module = "deleteEvent";
        if (eventId == null) {
            throw new IllegalArgumentException(module + " - EventId should not be null!");
        }

        SolrServer solrServer = findEventSolrServer();
        if (solrServer == null) {
            throw new EtrException(module + " - solrServer should not be null!");
        }

        try {
            solrServer.deleteById(String.valueOf(eventId));
            //solrServer.deleteByQuery("id:" + id);
            solrServer.commit();
        } catch (SolrServerException e) {
            throw new EtrException(e);
        } catch (IOException e) {
            throw new EtrException(e);
        }

    }

    //--------------------------------
    // Utils:

    /**
     * @param solrDocument
     * @return Event
     */
    private Event buildEventFromResultSet(SolrDocument solrDocument) throws SolrServerException {
        Event result = null;
        if (solrDocument != null) {
            result = new Event();

            // ID:
            Object responseId = solrDocument.getFieldValue("id");
            Long responseIdAsLong = null;
            if (responseId != null && responseId instanceof Long) {
                responseIdAsLong = Long.parseLong(responseId.toString());
                result.setId(responseIdAsLong);
            }

            Object titleObject = solrDocument.getFieldValue("title");
            if (titleObject != null) {
                result.setTitle(titleObject.toString());
            }

            // Set Category
            String categoryCode = null;
            Object categoryCodeObject = solrDocument.getFieldValue("category_code");
            if (categoryCodeObject != null) {
                categoryCode = categoryCodeObject.toString();
            }
            if (categoryCode != null) {
                Event.Category category = this.eventCategoryCache.lookupCategory(categoryCode, this.solrServerFactory);
                result.setCategory(category);
            }

            // Set Severity
            String severityCode = null;
            Object severityCodeObject = solrDocument.getFieldValue("severity");
            if (severityCodeObject != null) {
                severityCode = severityCodeObject.toString();
            }
            if (severityCode != null) {
                Event.Severity severity = Event.Severity.valueOf(severityCode);
                if (severity != null) {
                    result.setSeverity(severity);
                }
            }

            // Set source:
            Object sourceObject = solrDocument.getFieldValue("source");
            if (sourceObject != null) {
                result.setSource(sourceObject.toString());
            }

            // Set process id:
            Object processIdObject = solrDocument.getFieldValue("process_id");
            if (processIdObject != null) {
                result.setSource(processIdObject.toString());
            }

            Object createdDateObject = solrDocument.getFieldValue(EventEntityNames.Column.CREATED);
            if (createdDateObject != null && createdDateObject instanceof Date) {
                result.setCreated((Date) createdDateObject);
            }
        }

        return result;
    }

    private SolrServer findEventSolrServer() {
        return this.solrServerFactory.findSolrServer(SolrCoreCode.EVENTS);
    }

    private List<Event> loadEvents(SolrQuery solrQuery, SolrServer solrServer) {
        QueryResponse response = null;
        List<Event> events = new ArrayList<>();
        try {
            response = solrServer.query(solrQuery);
            SolrDocumentList results = response.getResults();

            LOG.info("o Events:");
            for (SolrDocument solrDocument : results) {
                events.add(buildEventFromResultSet(solrDocument));
            }
        } catch (SolrServerException e) {
            throw new EtrException(e);
        }
        return events;
    }


    private static SolrInputDocument addFieldForPartialUpdate(SolrInputDocument solrDocument, String fieldName, Object fieldValue) {
        Map<String, Object> partialUpdate = new HashMap<String, Object>();
        partialUpdate.put("set", fieldValue);
        solrDocument.addField(fieldName, partialUpdate);
        return solrDocument;
    }


}
