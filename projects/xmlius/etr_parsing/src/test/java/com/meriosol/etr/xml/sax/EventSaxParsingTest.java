package com.meriosol.etr.xml.sax;

import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Sample of how xml SAX events can be caught.<br>
 * Useful links: http://www.mkyong.com/java/how-to-read-utf-8-xml-file-in-java-sax-parser/
 *
 * @author meriosol
 * @version 0.2
 * @since 13/04/14
 */
public class EventSaxParsingTest {
    private static final Class<EventSaxParsingTest> MODULE = EventSaxParsingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String ETR_XSD_RESOURCE_PATH = "etr.xsd";

    /**
     * Sample events files.
     */
    interface SampleEventResource {
        String CORRECT_EVENTS_RESOURCE_PATH = "events.xml";
        String INVALID_EVENTS_RESOURCE_PATH = "events_invalid.xml";
        String BADLY_FORMED_EVENTS_RESOURCE_PATH = "events_badly_formed.xml";
    }

    @Test
    public void testCorrectEventsParse() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = SampleEventResource.CORRECT_EVENTS_RESOURCE_PATH;
        List<EventInfo> eventInfoList = SaxUtil.gatherEventInfo(eventsResourcePath);
        assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);

        lOG.info(String.format("~~~ Events(%s found): ", eventInfoList.size()));
        for (EventInfo eventInfo : eventInfoList) {
            lOG.info("~~~~~ event: " + eventInfo);
        }
    }

    @Test
    public void testCorrectEventsParseWithErrorHandler() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = SampleEventResource.CORRECT_EVENTS_RESOURCE_PATH;

        List<String> parsingMessagesHolder = new ArrayList<>();



        List<EventInfo> eventInfoList = SaxUtil.gatherEventInfo(eventsResourcePath, ETR_XSD_RESOURCE_PATH, parsingMessagesHolder);
        assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);

        logParserMessagesIfAny(parsingMessagesHolder);
        logEventsData("CorrectEventsParseWithErrorHandler",eventInfoList);
    }

    @Test (expected = SAXException.class)
    public void testInvalidEventsParseWithErrorHandler() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = SampleEventResource.INVALID_EVENTS_RESOURCE_PATH;

        List<String> parsingMessagesHolder = new ArrayList<>();

        List<EventInfo> eventInfoList = SaxUtil.gatherEventInfo(eventsResourcePath, ETR_XSD_RESOURCE_PATH, parsingMessagesHolder);
        // assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);
        // TODO: add xsd validation (etr.xsd).

        lOG.info(String.format("NOTE: because '%s' has xsd errors, parsingMessagesHolder should contain warnings. "
                + " For now it has '%s' messages..", eventsResourcePath, parsingMessagesHolder.size()));
        logParserMessagesIfAny(parsingMessagesHolder);
        logEventsData("InvalidEventsParseWithErrorHandler", eventInfoList);
    }

    @Test (expected = SAXException.class)
    public void testDeeplyScrewedEventsParseWithErrorHandler() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = SampleEventResource.BADLY_FORMED_EVENTS_RESOURCE_PATH;

        List<String> parsingMessagesHolder = new ArrayList<>();

        List<EventInfo> eventInfoList = SaxUtil.gatherEventInfo(eventsResourcePath, ETR_XSD_RESOURCE_PATH, parsingMessagesHolder);
        lOG.warning(String.format("Because '%s' has errors, code shouldn't reach here!", eventsResourcePath));
    }
    //---------------------
    // Utils

    /**
     * @param eventInfoList
     */
    private void logEventsData(String title, List<EventInfo> eventInfoList) {
        String delimLine = "\n~~~ ======================================\n";
        lOG.info(String.format("%s~~~ (%s) Events(%s found): ", delimLine, title, eventInfoList.size()));
        for (EventInfo eventInfo : eventInfoList) {
            lOG.info("~~~~~ event: " + eventInfo);
        }
        lOG.info(delimLine);
    }

    /**
     * @param parsingMessagesHolder
     */
    private void logParserMessagesIfAny(List<String> parsingMessagesHolder) {
        if (parsingMessagesHolder.size() > 0) {
            String delimLine = "\n~~~ ******************************\n";
            lOG.warning(String.format("%s~~~ SAX parser reported (%s) messages: ", delimLine, parsingMessagesHolder.size()));
            for (String message : parsingMessagesHolder) {
                lOG.warning("~~~~~ message: " + message);
            }
            lOG.info(delimLine);
        }
    }
}
