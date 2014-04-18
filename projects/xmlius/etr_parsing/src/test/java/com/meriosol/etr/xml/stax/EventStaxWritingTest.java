package com.meriosol.etr.xml.stax;

import com.meriosol.etr.domain.EventCategoryInfo;
import com.meriosol.etr.domain.EventInfo;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * XML building (using Stax) tests.
 * @author meriosol
 * @version 0.1
 * @since 17/04/14
 */
public class EventStaxWritingTest {
    private static final Class<EventStaxWritingTest> MODULE = EventStaxWritingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String EVENTS_XML_NS = "http://com/meriosol/etr/schema";
    private static final String DEFAULT_OUTPUT_DIR = System.getProperty("java.io.tmpdir");

    @Test
    public void testWritingEvents() throws IOException, XMLStreamException {
        String eventsOutputFileName = "etr_events_from_stax.xml";
        File outputFile = new File(DEFAULT_OUTPUT_DIR + File.separator + eventsOutputFileName);

        XMLEventWriter eventWriter = StaxUtil.createXMLEventWriterWithStartingDocForWriting(outputFile);

        // Create an EventFactory
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();

        // Namespaces:
//        Iterator<Namespace> namespaceIterator = StaxUtil.createNamespaces(eventFactory, "", EVENTS_XML_NS);
        Iterator<Namespace> namespaceIterator = null;

        // Create end event for moving to the next line:
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        // create and write Start Tag
        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);
        eventWriter.add(end);

        // create events open tag
        QName eventsQName = new QName(EVENTS_XML_NS, EtrFieldNames.EVENTS);
        StartElement eventsStartElement = eventFactory.createStartElement(eventsQName, namespaceIterator, null);


        eventWriter.add(eventsStartElement);
        eventWriter.add(end);
        eventWriter.add(eventFactory.createComment(" Events: "));
        eventWriter.add(end);

        SampleEventFactory sampleEventFactory = new SampleEventFactory();
        List<EventInfo> eventInfoList = sampleEventFactory.createSampleEvents();
        for (EventInfo eventInfo : eventInfoList) {
            // Attributes:
            Properties eventInfoProperties = eventInfo.getProperties();

            // Create ETR event with base attributes:
            StartElement eventStartElement = eventFactory.createStartElement("", EVENTS_XML_NS, EtrFieldNames.EVENT);

            eventWriter.add(tab);
            eventWriter.add(eventStartElement);
            if (eventInfoProperties != null) {
                // For ID(safety checks added):
                String idValue = eventInfoProperties.getProperty(EtrFieldNames.Event.ID);
                if (idValue != null) {
                    Attribute attributeId = eventFactory.createAttribute(EtrFieldNames.Event.ID, idValue);
                    eventWriter.add(attributeId);
                }

                // Shorter for 'created' (but can cause NPI, try commented code to see obscure NPI):
                //eventWriter.add(eventFactory.createAttribute(EtrFieldNames.Event.CREATED, null));
                eventWriter.add(eventFactory.createAttribute(EtrFieldNames.Event.CREATED
                        , eventInfoProperties.getProperty(EtrFieldNames.Event.CREATED)));
            }
            eventWriter.add(end);

            if (eventInfoProperties != null) {
                // Add event elements
                // CAUTION: bean properties are ignored, universal Properties field is used..not for PROD use..
                StaxUtil.createNode(eventWriter, EtrFieldNames.Event.TITLE, eventInfoProperties.getProperty(EtrFieldNames.Event.TITLE), EVENTS_XML_NS);
                StaxUtil.createNode(eventWriter, EtrFieldNames.Event.SOURCE, eventInfoProperties.getProperty(EtrFieldNames.Event.SOURCE), EVENTS_XML_NS);
            }

            // For complex elements - own handling(TODO: invent universal way)..
            //
            EventCategoryInfo eventCategoryInfo = eventInfo.getEventCategory();
            if (eventCategoryInfo != null) {
                StartElement eventCategoryStartElement = eventFactory.createStartElement("", EVENTS_XML_NS, EtrFieldNames.EVENT_CATEGORY);
                eventWriter.add(tab);
                eventWriter.add(eventCategoryStartElement);

                // Attributes:
                Properties eventCategoryInfoProperties = eventCategoryInfo.getProperties();
                if (eventCategoryInfoProperties != null) {
                    // For code:
                    String codeValue = eventCategoryInfoProperties.getProperty(EtrFieldNames.Event.Category.CODE);
                    if (codeValue != null) {
                        eventWriter.add(eventFactory.createAttribute(EtrFieldNames.Event.Category.CODE, codeValue));
                    }

                    // For name:
                    String nameValue = eventCategoryInfoProperties.getProperty(EtrFieldNames.Event.Category.NAME);
                    if (nameValue != null) {
                        eventWriter.add(end);
                        eventWriter.add(tab);
                        StaxUtil.createNode(eventWriter, EtrFieldNames.Event.Category.NAME, nameValue, EVENTS_XML_NS);
                    }
                }

                eventWriter.add(tab);
                eventWriter.add(eventFactory.createEndElement("", EVENTS_XML_NS, EtrFieldNames.EVENT_CATEGORY));
                eventWriter.add(end);
            }
            eventWriter.add(tab);
            eventWriter.add(eventFactory.createEndElement("", EVENTS_XML_NS, EtrFieldNames.EVENT));
            eventWriter.add(end);
        }

        eventWriter.add(eventFactory.createEndElement("", EVENTS_XML_NS, EtrFieldNames.EVENTS));
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
        //------------------------------

        lOG.info(String.format("Events were written into file '%s'.", outputFile.getAbsolutePath()));
    }


    /**
     * Creates sample ETR events
     */
    private class SampleEventFactory {
        private Random random = new Random();

        List<EventInfo> createSampleEvents() {
            List<EventInfo> eventInfoList = new ArrayList<>();
            eventInfoList.add(buildEventInfo(1000001L, "Event 1"));
            eventInfoList.add(buildEventInfo(1000002L, "Event 2"));
            return eventInfoList;
        }

        EventInfo buildEventInfo(Long id, String title) {
            EventInfo eventInfo = new EventInfo();
            //eventInfo.setTitle(title);
            Properties properties = new Properties();
            properties.put(EtrFieldNames.Event.ID, id.toString());
            properties.put(EtrFieldNames.Event.TITLE, title);
            properties.put(EtrFieldNames.Event.SOURCE, String.format("Testing_%d", random.nextInt(99)));
            properties.put(EtrFieldNames.Event.CREATED, String.format("2014-04-04T01:02:04.%dZ", random.nextInt(999)));
            eventInfo.addProperties(properties);

            eventInfo.setEventCategory(buildSampleEventCategoryInfo("MESSAGE"));

            return eventInfo;
        }

        EventCategoryInfo buildSampleEventCategoryInfo(String code) {
            EventCategoryInfo eventCategoryInfo = new EventCategoryInfo();
            eventCategoryInfo.setCode(code); //TODO: Well, duplication of putting fields again..

            Properties properties = new Properties();
            properties.put(EtrFieldNames.Event.Category.CODE, code);
            properties.put(EtrFieldNames.Event.Category.NAME, "Some categ");

            eventCategoryInfo.addProperties(properties);

            return eventCategoryInfo;
        }

    }


}
