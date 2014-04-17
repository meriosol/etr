package com.meriosol.etr.xml.stax;

import com.meriosol.etr.CommonUtil;
import com.meriosol.etr.domain.EventCategoryInfo;
import com.meriosol.etr.domain.EventInfo;
import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Sample of how xml StAX events can be iterated.<br>
 *
 * @author meriosol
 * @version 0.1
 * @since 16/04/14
 */
public class EventStaxParsingTest {
    private static final Class<EventStaxParsingTest> MODULE = EventStaxParsingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String ETR_XSD_RESOURCE_PATH = "etr.xsd"; // TODO: remove if not needed.

    private interface FieldNames {
        String EVENTS = "events";
        String EVENT = "event";
        String EVENT_CATEGORY = "event-category";
    }

    /**
     * Sample events files.
     */
    interface SampleEventResource {
        String CORRECT_EVENTS_RESOURCE_PATH = "events.xml";
        String INVALID_EVENTS_RESOURCE_PATH = "events_invalid.xml";
        String BADLY_FORMED_EVENTS_RESOURCE_PATH = "events_badly_formed.xml";
    }

    @Test
    public void testCorrectEventsParse() throws IOException, XMLStreamException {
        String eventsResourcePath = SampleEventResource.CORRECT_EVENTS_RESOURCE_PATH;
        XMLEventReader xmlEventReader = StaxUtil.createXMLEventReaderForResource(eventsResourcePath);
        assertNotNull(String.format("xmlEventReader should not be null for resource path '%s'!", eventsResourcePath), xmlEventReader);
        //==================================
        List<EventInfo> eventInfoList = new ArrayList<>();
        EventInfo eventInfo = null;
        // EventCategoryInfo eventCategoryInfo = null;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                // If we have an item element, we create a new item
                if (FieldNames.EVENT.equals(elementName)) {
                    eventInfo = new EventInfo();
                    StaxUtil.addAttributesToInfo(eventInfo, startElement.getAttributes());
                } else {
                    if (eventInfo != null) {
                        if (!FieldNames.EVENT_CATEGORY.equals(elementName)) {
                            StaxUtil.addChildSimpleElementDataToInfo(eventInfo, xmlEventReader, elementName);
                        } else {
                            EventCategoryInfo eventCategoryInfo = new EventCategoryInfo();
                            // Add attributes:
                            StaxUtil.addAttributesToInfo(eventCategoryInfo, startElement.getAttributes());
                            // Add simple child elements as properties:
                            StaxUtil.addChildSimpleElementsDataToInfo(eventCategoryInfo, xmlEventReader, startElement);
                            eventInfo.setEventCategory(eventCategoryInfo);
                        }
                    }
                }
            } else if (event.isEndElement()) {
                // If we reach the end of an item element, we add it to the list
                EndElement endElement = event.asEndElement();
                if (FieldNames.EVENT.equals(endElement.getName().getLocalPart())) {
                    eventInfoList.add(eventInfo);
                }
            }
        }
        assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);
        CommonUtil.logEventsData("CorrectEventsParse", eventInfoList);
        //==================================
    }

}
