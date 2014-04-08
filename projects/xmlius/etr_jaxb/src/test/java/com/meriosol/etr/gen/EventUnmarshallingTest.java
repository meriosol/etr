package com.meriosol.etr.gen;

import com.meriosol.jaxb.JaxbRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * NOTE: using <code>EtrUnmarshallHelper</code> allows to forget about JAXB plumbing almost completely(you still deal with JAXB generated POJOs).
 *
 * @author meriosol
 * @version 0.1
 * @since 07/04/14
 */
public class EventUnmarshallingTest {
    private static final Class<EventUnmarshallingTest> MODULE = EventUnmarshallingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private EventsPrinter eventsPrinter;

    @Before
    public void setUp() {
        this.eventsPrinter = new EventsPrinter();
    }

    @After
    public void tearDown() throws Exception {
        this.eventsPrinter = null;
    }


    @Test
    public void testCorrectEventsUnmarshall() {
        String eventsResourcePath = "events.xml";
        boolean validationErrorTolerant = false;
        EtrUnmarshallHelper<Events> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
        Events events = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventsResourcePath, Events.class);
        assertNotNull(String.format("Sample events should not be null for resource path '%s'!", eventsResourcePath), events);
        StringBuilder eventsInfo = this.eventsPrinter.printEvents(events);
        lOG.info(eventsInfo.toString());
    }

    @Test
    public void testCorrectEventUnmarshall() {
        String eventResourcePath = "event.xml";
        boolean validationErrorTolerant = false;
        EtrUnmarshallHelper<Event> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
        Event event = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventResourcePath, Event.class);
        assertNotNull(String.format("Sample event should not be null for resource path '%s'!", eventResourcePath), event);
        String eventInfo = this.eventsPrinter.printEvent(event);
        lOG.info(eventInfo);
    }

    @Test
    public void testCorrectEventCategoriesUnmarshall() {
        String eventCategoriesResourcePath = "event_categories.xml";
        boolean validationErrorTolerant = false;
        EtrUnmarshallHelper<EventCategories> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
        EventCategories eventCategories = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventCategoriesResourcePath, EventCategories.class);
        assertNotNull(String.format("Sample eventCategories should not be null for resource path '%s'!", eventCategoriesResourcePath), eventCategories);
        StringBuilder eventCategoriesInfo = this.eventsPrinter.printEventCategories(eventCategories);
        lOG.info(eventCategoriesInfo.toString());
    }

    @Test
    public void testCorrectEventCategoryUnmarshall() {
        String eventCategoryResourcePath = "event_category.xml";
        boolean validationErrorTolerant = false;
        EtrUnmarshallHelper<EventCategory> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
        EventCategory eventCategory = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventCategoryResourcePath, EventCategory.class);
        assertNotNull(String.format("Sample eventCategory should not be null for resource path '%s'!", eventCategoryResourcePath), eventCategory);
        String eventCategoryInfo = this.eventsPrinter.printEventCategory(eventCategory);
        lOG.info(eventCategoryInfo);
    }

    @Test(expected = JaxbRuntimeException.class)
    public void testIncorrectExistentEventsUnmarshall() {
        String eventsResourcePath = "incorrect_events.xml";
        boolean validationErrorTolerant = false;
        EtrUnmarshallHelper<Events> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
        Events events = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventsResourcePath, Events.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullResourcePathEventsUnmarshall() {
        String eventsResourcePath = null;
        boolean validationErrorTolerant = false;
        EtrUnmarshallHelper<Events> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
        Events events = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventsResourcePath, Events.class);
    }
    //----------------------------------------------
    // Utils

    private class EventsPrinter {
        StringBuilder printEvents(Events events) {
            StringBuilder sb = null;
            List<Event> eventList = events.getEvent();
            if (eventList != null) {
                sb = new StringBuilder(String.format("\n" +
                        ">================================\n  Events(%d found): \n", eventList.size()));
                for (int i = 0; i < eventList.size(); i++) {
                    Event event = eventList.get(i);
                    sb.append(" o ").append(printEvent(event)).append("\n");
                }
                sb.append("<================================\n");
            }

            return sb;

        }

        StringBuilder printEventCategories(EventCategories eventCategories) {
            StringBuilder sb = null;
            List<EventCategory> eventCategoryList = eventCategories.getEventCategory();
            if (eventCategoryList != null) {
                sb = new StringBuilder(String.format("\n" +
                        ">================================\n  Event categories(%d found): \n", eventCategoryList.size()));
                for (int i = 0; i < eventCategoryList.size(); i++) {
                    EventCategory eventCategory = eventCategoryList.get(i);
                    sb.append(" o ").append(printEventCategory(eventCategory)).append("\n");
                }
                sb.append("<================================\n");
            }

            return sb;

        }

        String printEvent(Event event) {
            return String.format("Event:{id='%s', title='%s', created='%s', category='%s', severity='%s'}"
                    , event.getId(), event.getTitle(), event.getCreated()
                    , printEventCategory(event.getEventCategory()), event.getSeverity());
        }

        String printEventCategory(EventCategory eventCategory) {
            return String.format("EventCategory:{code='%s', name='%s'}"
                    , eventCategory.getCode(), eventCategory.getName());
        }

    }
}
