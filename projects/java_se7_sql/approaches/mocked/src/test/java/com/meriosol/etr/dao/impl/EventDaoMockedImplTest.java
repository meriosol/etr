package com.meriosol.etr.dao.impl;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.domain.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * TODO: add methods testing smth specific in impl. As for general testing, check apistudy/test test cases.
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class EventDaoMockedImplTest {
    private EventDao eventDao;

    @Before
    public void setUp() {
        this.eventDao = new EventDaoMockedImpl();
    }

    @After
    public void tearDown() throws Exception {
        this.eventDao = null;
    }

    @Test
    public void testEventCreate() {
        assertNotNull("EventDao should not be null!", this.eventDao);
        // Create event:
        Event.Category category = new Event.Category("MESSAGE", "");
        Event.Severity severity = Event.Severity.INFO;
        String source = "sample_src";
        String processId = "sample_proc";
        String title = "sample_title";

        Event inputEvent = new Event(category, severity, source, processId, title);
        Event event = this.eventDao.create(inputEvent);
        assertNotNull("Event should not be null!", event);
        assertNotNull("Event ID should not be null!", event.getId());
        assertNotNull("Event title should not be null!", event.getTitle());
    }
}
