package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;

import java.util.*;

/**
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class EventDaoMockedImpl implements EventDao {
    private static final Random RANDOM = new Random();
    private static final SampleEventBuilder SAMPLE_EVENT_BUILDER = new SampleEventBuilder();
    /**
     * This event ID is mentioned in different unit tests as expected.
     */
    private static final Long EXPECTED_DEFAULT_EVENT_ID = 1000001L;
    /**
     * Primitive "DB" for keeping temp events created while testing.
     */
    private Map<Long, Event> eventDB = new HashMap<>();

    public EventDaoMockedImpl() {
        initEventDB();
    }

    private void initEventDB() {
        this.eventDB = new HashMap<>();
        Event defaultEvent = new Event();
        defaultEvent.setId(EXPECTED_DEFAULT_EVENT_ID);
        this.eventDB.put(defaultEvent.getId(), defaultEvent);
    }

    @Override
    public String getDaoName() {
        return "MockedImpl";
    }

    @Override
    public String getDaoDescription() {
        return "Mocked implementation. No real DB access is performed.";
    }

    /**
     * @param event
     * @return Created event (id should appear in this object).
     */
    @Override
    public Event create(Event event) {
        if (event != null && event.getId() == null) {
            event.setId(createNextId());
            this.eventDB.put(event.getId(), event);
        }
        return event;
    }

    /**
     * @param eventId
     * @return Event.
     */
    @Override
    public Event retrieveEvent(Long eventId) {
        return eventDB.get(eventId);
        // return SAMPLE_EVENT_BUILDER.buildEvent(eventId);
    }

    /**
     * @param maxEventCount How many event to return. Sorting should be by 'created' desc.
     * @return List of events.
     */
    @Override
    public List<Event> retrieveRecentEvents(Integer maxEventCount) {
        List<Event> events = new ArrayList<>();
        if (maxEventCount != null && maxEventCount > 0) {
            for (long i = 1; i <= maxEventCount; i++) {
                events.add(SAMPLE_EVENT_BUILDER.buildEvent(createNextId()));
            }
        }

        return events;
    }

    /**
     * @param startDate Start date (exclusive)
     * @param endDate   End date (inclusive)
     * @return List of events. Sorting should be by 'created' desc.
     */
    @Override
    public List<Event> retrieveEventsForPeriod(Date startDate, Date endDate) {
        // NOTE: It's important to set created field value really between startDate and endDate. Unit tests can set this invariant..
        List<Event> events = new ArrayList<>();
        if (startDate != null && endDate != null) {
            if (startDate.getTime() == endDate.getTime()) {
                throw new IllegalArgumentException("Incorrect input parameters: startDate and endDate should not be the same!");
            } else if (startDate.getTime() > endDate.getTime()) {
                throw new IllegalArgumentException("Incorrect input parameters: startDate should be < endDate!");
            }

        }

        int iterationCount = 10;
        // Logic: if endDate is not empty { let's gradually start reducing time.
        // If we reach either iterationCount or startDate, stop. }
        // else if startDate is not empty { let's jump iterationCount
        // and gradually reduce time till reach either iterationCount or startDate }
        //
        long createdDateMilliss = 0;
        boolean timeToBreak = false;
        for (int i = 1; i <= iterationCount; i++) {
            if (endDate != null) {
                long endDateMilliss = endDate.getTime();
                if (createdDateMilliss <= 0) {
                    createdDateMilliss = endDateMilliss - 1;
                } else {
                    createdDateMilliss--;
                }
                if (startDate != null) {
                    long startDateMilliss = startDate.getTime();
                    if (startDateMilliss >= createdDateMilliss) {
                        createdDateMilliss++; // get back a bit cause startDate is 'exclusive'.
                        if (createdDateMilliss > endDateMilliss) {
                            // if we went too far, let's get back to end.
                            createdDateMilliss = endDateMilliss;
                        }
                        timeToBreak = true;
                    }
                }
            } else if (startDate != null) {
                if (createdDateMilliss <= 0) {
                    long startDateMilliss = startDate.getTime();
                    createdDateMilliss = startDateMilliss + iterationCount;
                } else {
                    createdDateMilliss--;
                }

//                long endDateMilliss = endDate.getTime();
//                if (createdDateMilliss > endDateMilliss) {
//                    createdDateMilliss = endDateMilliss;
//                    timeToBreak = true;
//                }
            }

            if (createdDateMilliss > 0) {
                events.add(SAMPLE_EVENT_BUILDER.buildEvent(createNextId(), new Date(createdDateMilliss)));
            }
            if (timeToBreak) {
                break;
            }
        }
        return events;
    }

    /**
     * @param event
     * @return Updated event(normally exactly the same object).
     */
    @Override
    public Event update(Event event) {
        if (event != null) {
            this.eventDB.put(event.getId(), event);
        }

        return event;
    }

    /**
     * Deletes event.
     *
     * @param event
     */
    @Override
    public void delete(Event event) {
        if (event != null) {
            this.eventDB.remove(event.getId());
        }
    }

    /**
     * Deletes event.
     *
     * @param eventId
     */
    @Override
    public void deleteEvent(Long eventId) {
        if (eventId != null) {
            this.eventDB.remove(eventId);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.eventDB.clear();
        this.eventDB = null;
    }

    //--------------------------------------
    // Utils:

    private static Long createNextId() {
        return RANDOM.nextLong();
    }

    private static class SampleEventBuilder {
        /**
         * @param id
         * @param created
         * @return Event
         */
        Event buildEvent(Long id, Date created) {
            Event event = buildEvent(id);
            event.setCreated(created);
            return event;
        }

        /**
         * @param id
         * @return Event
         */
        Event buildEvent(Long id) {
            Event.Category category = new Event.Category("MESSAGE", "");
            Event.Severity severity = Event.Severity.INFO;
            String source = "mocked_src";
            String processId = "mocked_proc";
            String title = "mocked_title";
            Date created = new Date();

            Event event = new Event();
            event.setId(id);
            event.setCategory(category);
            event.setSeverity(severity);
            event.setSource(source);
            event.setProcessId(processId);
            event.setTitle(title);
            event.setCreated(created);

            return event;
        }
    }

}
