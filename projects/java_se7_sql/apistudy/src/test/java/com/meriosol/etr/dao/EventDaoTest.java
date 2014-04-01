package com.meriosol.etr.dao;

import com.meriosol.etr.domain.Event;
import com.meriosol.exception.EtrException;
import com.meriosol.performance.PerformanceTracker;
import com.meriosol.util.DateUtil;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class EventDaoTest {
    private static Logger LOG = null;
    private static final Random RANDOM = new Random();
    public PerformanceTracker performanceTracker;
    private EventDao eventDao;
    private SampleEventBuilder sampleEventBuilder;

    @BeforeClass
    public static void init() {
        LOG = LoggerFactory.getLogger(EventDaoTest.class);
    }

    @Before
    public void setUp() {
        EventDaoFactory eventDaoFactory = EventDaoFactory.getInstance();
        if (eventDaoFactory != null) {
            this.eventDao = eventDaoFactory.loadEventDao();
            assert this.eventDao != null;
            LOG.info(">>>> EventDao loaded. Name='{}'. Details: {}", this.eventDao.getDaoName(), this.eventDao.getDaoDescription());
        }
        this.sampleEventBuilder = new SampleEventBuilder();
        this.performanceTracker = new PerformanceTracker(this.getClass().getSimpleName() + "__" + this.eventDao.getDaoName());
        this.performanceTracker.startTracking();
    }

    @After
    public void tearDown() throws Exception {
        this.eventDao = null;
        if (this.performanceTracker != null) {
            this.performanceTracker.finishTracking();
            LOG.info("Performance report:\n{}\n", this.performanceTracker.emitReport());
        }
    }

    @Test @Ignore
    public void testEventCreate() {
        Event event = createTestingEvent();
        assertNotNull("Event should not be null!", event);
    }

    @Test
    public void testEventRetrieveById() {
        final String module = "testEventRetrieveById";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            // CAUTION: DB should be initialized with events starting from 1000001.
            Long sampleEventId = 1000001L;

            LOG.info("BEFORE Retrieve event by id='{}'...", sampleEventId);
            Event event = eventDao.retrieveEvent(sampleEventId);
            assertNotNull(String.format("Event for ID '%s' should not be null!", sampleEventId), event);
            assertEquals(String.format("Event.id '%s' should be the same as sampleEventId='%s'!"
                    , event.getId(), sampleEventId), event.getId(), sampleEventId);
            LOG.info("AFTER Retrieve event by id='{}'. Event: {}", sampleEventId, event);
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test
    public void testRecentEventsRetrieveWithMaxLimit() {
        final String module = "testRecentEventsRetrieveWithMaxLimit";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            Integer maxEventCount = 5;
            List<Event> events = eventDao.retrieveRecentEvents(maxEventCount);
            assertNotNull(String.format("Events for maxEventCount '%s' should not be null!", maxEventCount), events);
            assertTrue(String.format("Event list size should be <= maxEventCount '%s' but it's = '%s'!"
                    , maxEventCount, events.size()), events.size() <= maxEventCount);
            LOG.info("AFTER Retrieve events with max count='{}'. '{}' events found. Details: {}", maxEventCount
                    , events.size(), constructLogStringForEvents(events));
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test @Ignore
    public void testEventsRetrieveForPeriodWithBothPartsNotNull() throws ParseException {
        final String module = "testEventsRetrieveForPeriodWithBothPartsNotNull";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            Date endDate = new Date();
            Date startDate = new Date(endDate.getTime() - 1000 * 60 * 60 * 24 * 3); // 3 days shift, basically

            List<Event> events = eventDao.retrieveEventsForPeriod(startDate, endDate);
            assertNotNull(String.format("Events for period '%s'-'%s' should not be null!"
                    , DateUtil.formatDateWithDefaultFormat(startDate), DateUtil.formatDateWithDefaultFormat(endDate)), events);
            for (Event event : events) {
                assertNotNull("Event must not be null!", event);
                Date created = event.getCreated();
                assertNotNull(String.format("created for event with ID '%s' should not be null!", event.getId()), created);
                long createdTime = created.getTime();
                assertTrue(String.format("Created (%s) for event with ID '%s' should be > startDate(%s)!"
                        , DateUtil.formatDateWithDefaultFormat(created), event.getId()
                        , DateUtil.formatDateWithDefaultFormat(startDate)), createdTime > startDate.getTime());
                assertTrue(String.format("Created (%s) for event with ID '%s' should be <= endDate(%s)!"
                        , DateUtil.formatDateWithDefaultFormat(created), event.getId()
                        , DateUtil.formatDateWithDefaultFormat(endDate)), createdTime <= endDate.getTime());
            }
            LOG.info("AFTER Retrieve events for a range startDate='{}', endDate='{}'. '{}' events found. Details: {}"
                    , DateUtil.formatDateWithDefaultFormat(startDate)
                    , DateUtil.formatDateWithDefaultFormat(endDate), events.size(), constructLogStringForEvents(events));
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test @Ignore
    public void testEventsRetrieveForPeriodWithEndDateNotNull() throws ParseException {
        final String module = "testEventsRetrieveForPeriodWithEndDateNotNull";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            Date endDate = new Date();
            Date startDate = null;

            List<Event> events = eventDao.retrieveEventsForPeriod(startDate, endDate);
            assertNotNull(String.format("Events for period '%s'-'%s' should not be null!"
                    , DateUtil.formatDateWithDefaultFormat(startDate), DateUtil.formatDateWithDefaultFormat(endDate)), events);
            for (Event event : events) {
                assertNotNull("Event must not be null!", event);
                Date created = event.getCreated();
                assertNotNull(String.format("created for event with ID '%s' should not be null!", event.getId()), created);
                long createdTime = created.getTime();
                assertTrue(String.format("Created (%s) for event with ID '%s' should be <= endDate(%s)!"
                        , DateUtil.formatDateWithDefaultFormat(created), event.getId()
                        , DateUtil.formatDateWithDefaultFormat(endDate)), createdTime <= endDate.getTime());
            }
            LOG.info("AFTER Retrieve events for a range startDate='{}', endDate='{}'. '{}' events found. Details: {}"
                    , DateUtil.formatDateWithDefaultFormat(startDate), DateUtil.formatDateWithDefaultFormat(endDate)
                    , events.size(), constructLogStringForEvents(events));
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test @Ignore
    public void testEventsRetrieveForPeriodWithStartDateNotNull() throws ParseException {
        final String module = "testEventsRetrieveForPeriodWithStartDateNotNull";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            Date endDate = null;
            Date startDate = new Date((new Date()).getTime() - 1000 * 60 * 60 * 24 * 3); // 3 days shift, basically

            List<Event> events = eventDao.retrieveEventsForPeriod(startDate, endDate);
            assertNotNull(String.format("Events for period '%s'- should not be null!"
                    , DateUtil.formatDateWithDefaultFormat(startDate)), events);
            for (Event event : events) {
                assertNotNull("Event must not be null!", event);
                Date created = event.getCreated();
                assertNotNull(String.format("created for event with ID '%s' should not be null!", event.getId()), created);
                long createdTime = created.getTime();
                assertTrue(String.format("Created (%s) for event with ID '%s' should be > startDate(%s)!"
                        , DateUtil.formatDateWithDefaultFormat(created), event.getId()
                        , DateUtil.formatDateWithDefaultFormat(startDate)), createdTime > startDate.getTime());
            }
            LOG.info("AFTER Retrieve events for a range startDate='{}', endDate='{}'. '{}' events found. Details: {}"
                    , DateUtil.formatDateWithDefaultFormat(startDate), DateUtil.formatDateWithDefaultFormat(endDate)
                    , events.size(), constructLogStringForEvents(events));
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test @Ignore
    public void testEventsRetrieveForPeriodWithAllDatesNull() throws ParseException {
        final String module = "testEventsRetrieveForPeriodWithAllDatesNull";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            Date endDate = null;
            Date startDate = null;

            List<Event> events = eventDao.retrieveEventsForPeriod(startDate, endDate);
            assertNotNull(String.format("Events should not be null!", events));
            for (Event event : events) {
                assertNotNull("Event should not be null!", event);
                Date created = event.getCreated();
                assertNotNull(String.format("created for event with ID '%s' should not be null!", event.getId()), created);
            }
            LOG.info("AFTER Retrieve events for empty range startDate-endDate. {}", constructLogStringForEvents(events));
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test @Ignore
    public void testEventCreateAndUpdate() {
        final String module = "testEventCreateAndUpdate";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            // 1. Create event:
            Event event = createTestingEvent();
            this.performanceTracker.addMilestone(module + ".AFTER_createTestingEvent");
            // 2. Now update title of this event:
            String dateString = "";
            try {
                dateString = DateUtil.formatDateWithDefaultFormat(new Date());
            } catch (ParseException e) {
                throw new EtrException("Parse Exception", e);
            }

            LOG.info("AFTER Create event: {}", event);
            String newTitle = String.format("Updated_title(%s)", dateString);
            event.setTitle(newTitle);
            Event updatedEvent = this.eventDao.update(event);
            this.performanceTracker.addMilestone(module + ".AFTER_event_update");
            assertNotNull("updatedEvent should not be null!", updatedEvent);
            assertNotNull("updatedEvent.id should not be null!", updatedEvent.getId());
            assertTrue(String.format("updatedEvent should have title '%s', but has '%s'!", newTitle, updatedEvent.getTitle())
                    , newTitle.equals(updatedEvent.getTitle()));
            assertTrue(String.format("updatedEvent should have have the same id, as initial '%s' (but has '%s')!", event.getId(), updatedEvent.getId())
                    , event.getId().equals(updatedEvent.getId()));
            LOG.info("AFTER Update of created event: {}", updatedEvent);
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    @Test @Ignore
    public void testEventCreateAndDelete() {
        final String module = "testEventCreateAndDelete";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            // 1. Create event:
            Event event = createTestingEvent();
            this.performanceTracker.addMilestone(module + ".AFTER_createTestingEvent");
            LOG.info("BEFORE Delete of created event: {}", event);
            // 2. Now delete this event:
            this.eventDao.delete(event);
            this.performanceTracker.addMilestone(module + ".AFTER_event_delete");

            Event deletedEvent = eventDao.retrieveEvent(event.getId());
            assertNull(String.format("Deleted Event for ID '%s' should be null!", event.getId()), deletedEvent);
        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    /**
     * Reusable code for creating testing events.
     *
     * @return Testing event
     */
    private Event createTestingEvent() {
        assertNotNull("EventDao should not be null!", this.eventDao);
        // Create event:
        Event inputEvent = sampleEventBuilder.buildEvent();
        assertNotNull("inputEvent should not be null!", inputEvent);
        assertNull("inputEvent.id should be null!", inputEvent.getId());

        Event event = this.eventDao.create(inputEvent);
        assertNotNull("Event should not be null!", event);
        assertNotNull("Event ID should not be null!", event.getId());
        assertNotNull("Event title should not be null!", event.getTitle());

        LOG.info("AFTER Create new event. Event: {}", event);
        return event;
    }

    private String constructLogStringForEvents(List<Event> events) {
        StringBuilder sb = new StringBuilder();
        if (events != null && events.size() > 0) {
            sb.append("\n>-----------------\n");
            sb.append("  Events: \n");
            for (Event event : events) {
                sb.append(String.format("  o: %s \n", event));
            }
            sb.append("<-----------------\n");
        }

        return sb.toString();
    }

    /**
     * Sample event builder.
     */
    private static class SampleEventBuilder {
        /**
         * @param created
         * @return Event
         */
        Event buildEvent(Date created) {
            Event event = buildEvent();
            event.setCreated(created);
            return event;
        }

        /**
         * @return Event
         */
        Event buildEvent() {
            Event.Category category = new Event.Category("MESSAGE", "");

            Event.Severity severity = Event.Severity.INFO;
            String source = "sample_src";
            String processId = "sample_proc";
            String title = "sample_title__" + Math.abs(RANDOM.nextInt());
            Date created = new Date();

            Event event = new Event();
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
