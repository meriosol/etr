package com.meriosol.etr.xml.sax;

import com.meriosol.etr.xml.sax.handling.EventsSaxHandler;
import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Sample of how xml SAX event can be caought.<br>
 * Useful links: http://www.mkyong.com/java/how-to-read-utf-8-xml-file-in-java-sax-parser/
 *
 * @author meriosol
 * @version 0.1
 * @since 13/04/14
 */
public class EventSaxParsingTest {
    private static final Class<EventSaxParsingTest> MODULE = EventSaxParsingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private SAXParser saxParser;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException {
        initSax();
    }

    @After
    public void tearDown() throws Exception {
        this.saxParser = null;
    }


    @Test
    public void testCorrectEventsParse() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = "events.xml";
        URL eventsResourceUrl = MODULE.getClassLoader().getResource(eventsResourcePath);

        EventsSaxHandler eventsSaxHandler = new EventsSaxHandler();
        saxParser.parse(eventsResourceUrl.openStream(), eventsSaxHandler);

        List<EventInfo> eventInfoList = eventsSaxHandler.getEventInfoList();
        assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);

        lOG.info(String.format("~~~ Events(%s found): ", eventInfoList.size()));
        for (EventInfo eventInfo : eventInfoList) {
            lOG.info("~~~~~ event: " + eventInfo);
        }
    }


    //----------------------------------------------
    // Utils

    private void initSax() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        this.saxParser = factory.newSAXParser();
        try {
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "file, jar:file");
        } catch (IllegalArgumentException e) {
            //jaxp 1.5 feature not supported
            lOG.warning("Looks like jaxp 1.5 feature not supported: " + e.getMessage());
        }

    }

}
