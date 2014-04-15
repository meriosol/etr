package com.meriosol.etr.gen;

import com.meriosol.jaxb.XMLCalendarConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author meriosol
 * @version 0.1
 * @since 07/04/14
 */
public class EventMarshallingTest {
    private static final Class<EventMarshallingTest> MODULE = EventMarshallingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String DEFAULT_OUTPUT_DIR = System.getProperty("java.io.tmpdir");

    SampleEventsBuilder sampleEventsBuilder;

    @Before
    public void setUp() {
        this.sampleEventsBuilder = new SampleEventsBuilder();
    }

    @After
    public void tearDown() throws Exception {
        this.sampleEventsBuilder = null;
    }

    @Test
    public void testEventsMarshalling() throws JAXBException, FileNotFoundException {
        String eventsOutputFileName = "events.out.xml";
        File file = new File(DEFAULT_OUTPUT_DIR + File.separator + eventsOutputFileName);
        boolean validationErrorTolerant = false;
        EtrMarshallHelper<Events> etrMarshallHelper = new EtrMarshallHelper<>(validationErrorTolerant);

        Events sampleEvents = this.sampleEventsBuilder.createSampleEvents();
        etrMarshallHelper.marshall(file, sampleEvents);

        lOG.info("Events POJO was marshalled into file " + file.getAbsolutePath());
    }

    @Test
    public void testEventMarshalling() throws JAXBException, FileNotFoundException {
        String eventOutputFileName = "event.out.xml";

        File file = new File(DEFAULT_OUTPUT_DIR + File.separator + eventOutputFileName);
        boolean validationErrorTolerant = false;
        EtrMarshallHelper<Event> etrMarshallHelper = new EtrMarshallHelper<>(validationErrorTolerant);

        Event sampleEvent = this.sampleEventsBuilder.createSampleEvent(1000021L, "Some event test001", "MESSAGE");
        etrMarshallHelper.marshall(file, sampleEvent);

        lOG.info("EventState POJO was marshalled into file " + file.getAbsolutePath());
    }

    @Test(expected = NullPointerException.class)
    public void testEventWIthNullIdMarshalling() throws JAXBException, FileNotFoundException {
        String eventOutputFileName = "event.out.xml";

        File file = new File(DEFAULT_OUTPUT_DIR + File.separator + eventOutputFileName);
        boolean validationErrorTolerant = false;
        EtrMarshallHelper<Event> etrMarshallHelper = new EtrMarshallHelper<>(validationErrorTolerant);
        Long id = null;
        Event sampleEvent = this.sampleEventsBuilder.createSampleEvent(id, "Some event with null ID", "MESSAGE");
        etrMarshallHelper.marshall(file, sampleEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEventMarshallingToNullFile() throws JAXBException, FileNotFoundException {
        File file = null;
        boolean validationErrorTolerant = false;
        EtrMarshallHelper<Event> etrMarshallHelper = new EtrMarshallHelper<>(validationErrorTolerant);

        Event sampleEvent = this.sampleEventsBuilder.createSampleEvent(1000021L, "Some event test001 for null file", "MESSAGE");
        etrMarshallHelper.marshall(file, sampleEvent);
    }

    private class SampleEventsBuilder {
        ObjectFactory objectFactory = new ObjectFactory();

        Events createSampleEvents() {
            Events events = objectFactory.createEvents();
            List<Event> eventList = events.getEvent();
            eventList.add(createSampleEvent(1000001L, "Some event test1", "MESSAGE"));
            eventList.add(createSampleEvent(1000002L, "Some other test2", "MESSAGE"));
            return events;
        }

        Event createSampleEvent(Long eventId, String title, String categoryCode) {
            Event event = objectFactory.createEvent();
            event.setId(eventId);
            event.setTitle(title);
            event.setSource("MarshallTester");
            event.setProcessId("PR1");
            event.setSeverity("INFO");
            event.setCreated(XMLCalendarConverter.convertToXmlDate(new Date()));
            event.setEventCategory(createSampleEventCategory(categoryCode));
            return event;
        }

        EventCategory createSampleEventCategory(String code) {
            EventCategory eventCategory = objectFactory.createEventCategory();
            eventCategory.setCode(code);
            return eventCategory;
        }
    }

}
