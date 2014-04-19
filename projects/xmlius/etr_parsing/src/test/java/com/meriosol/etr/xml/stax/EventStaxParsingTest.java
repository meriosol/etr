package com.meriosol.etr.xml.stax;

import com.meriosol.etr.CommonUtil;
import com.meriosol.etr.domain.EventCategoryInfo;
import com.meriosol.etr.domain.EventInfo;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    private static final String ETR_XSD_RESOURCE_PATH = "etr.xsd";

    @Test
    public void testCorrectEventsParse() throws IOException, XMLStreamException {
        String eventsResourcePath = SampleEventResources.CORRECT_EVENTS_RESOURCE_PATH;
        XMLEventReader xmlEventReader = StaxUtil.createXMLEventReaderForResource(eventsResourcePath);
        assertNotNull(String.format("xmlEventReader should not be null for resource path '%s'!", eventsResourcePath), xmlEventReader);
        List<EventInfo> eventInfoList = assembleEventsInfo(xmlEventReader);
        assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);
        CommonUtil.logEventsData("CorrectEventsParse", eventInfoList);
        //==================================
    }

    @Test(expected = SAXException.class)
    public void testInvalidEventsParse() throws IOException, XMLStreamException, SAXException {
        String eventsResourcePath = SampleEventResources.INVALID_EVENTS_RESOURCE_PATH;
        String xsdResourcePath = ETR_XSD_RESOURCE_PATH;
        XMLEventReader xmlEventReader = StaxUtil.createXMLEventReaderForResource(eventsResourcePath);
        List<EventInfo> eventInfoList = null;
        // Now attempt to validate against xsd:
        StaxUtil.validateAgainstSchema(xmlEventReader, xsdResourcePath);
        eventInfoList = assembleEventsInfo(xmlEventReader);
        assertNull(String.format("Actually we must not reach this point for eventInfoList "
                + "because XML '%s' was designed to be invalid against schema '%s'.", eventsResourcePath, xsdResourcePath), eventInfoList);
    }

    @Test(expected = XMLStreamException.class)
    public void testBadlyFormedEventsParse() throws IOException, XMLStreamException {
        String eventsResourcePath = SampleEventResources.BADLY_FORMED_EVENTS_RESOURCE_PATH;
        XMLEventReader xmlEventReader = StaxUtil.createXMLEventReaderForResource(eventsResourcePath);
        List<EventInfo> eventInfoList = assembleEventsInfo(xmlEventReader);
        assertNull(String.format("Actually we must not reach this point for eventInfoList "
                + "because XML '%s' was designed to be badly bad guy.", eventsResourcePath), eventInfoList);
    }

    //--------------------------------------
    // Utils:

    /**
     * @param xmlEventReader
     * @return assembled events info
     * @throws XMLStreamException
     */
    private List<EventInfo> assembleEventsInfo(XMLEventReader xmlEventReader) throws XMLStreamException {
        List<EventInfo> eventInfoList = new ArrayList<>();
        EventInfo eventInfo = null;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                // If we have an item element, we create a new item
                if (EtrFieldNames.EVENT.equals(elementName)) {
                    eventInfo = new EventInfo();
                    StaxUtil.addAttributesToInfo(eventInfo, startElement.getAttributes());
                } else {
                    if (eventInfo != null) {
                        if (!EtrFieldNames.EVENT_CATEGORY.equals(elementName)) {
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
                if (EtrFieldNames.EVENT.equals(endElement.getName().getLocalPart())) {
                    eventInfoList.add(eventInfo);
                }
            }
        }
        return eventInfoList;
    }

}