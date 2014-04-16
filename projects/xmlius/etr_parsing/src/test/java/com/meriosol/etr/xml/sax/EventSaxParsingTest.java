package com.meriosol.etr.xml.sax;

import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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

    @Test
    public void testCorrectEventsParse() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = "events.xml";
        List<EventInfo> eventInfoList = SaxUtil.gatherEventInfo(eventsResourcePath);
        assertNotNull(String.format("Sample events eventInfoList should not be null for resource path '%s'!", eventsResourcePath), eventInfoList);

        lOG.info(String.format("~~~ Events(%s found): ", eventInfoList.size()));
        for (EventInfo eventInfo : eventInfoList) {
            lOG.info("~~~~~ event: " + eventInfo);
        }
    }
}
