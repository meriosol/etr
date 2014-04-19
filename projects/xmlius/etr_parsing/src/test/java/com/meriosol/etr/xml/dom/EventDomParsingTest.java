package com.meriosol.etr.xml.dom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Sample of how xml can be loaded as DOM tree.<br>
 * Useful links: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 *
 * @author meriosol
 * @version 0.1
 * @since 13/04/14
 */
public class EventDomParsingTest {
    private static final Class<EventDomParsingTest> MODULE = EventDomParsingTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String EVENTS_XML_NS = "http://com/meriosol/etr/schema";
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
    public void testCorrectEventsParse() throws ParserConfigurationException, IOException, SAXException {
        String eventsResourcePath = "events.xml";
        Document eventsDoc = DomUtil.loadDom(eventsResourcePath);

        assertNotNull(String.format("Sample events DOM tree doc should not be null for resource path '%s'!", eventsResourcePath), eventsDoc);
        eventsDoc.getDocumentElement().normalize();

        lOG.info("~~~ Sample events DOM tree root element: " + eventsDoc.getDocumentElement().getNodeName());

        StringBuilder eventsInfo = this.eventsPrinter.printEvents(eventsDoc);
        lOG.info(eventsInfo.toString());
    }


    //----------------------------------------------
    // Utils

    /**
     * Basic events data printer
     */
    private class EventsPrinter {
        /**
         * @param eventsDoc
         * @return events info
         */
        StringBuilder printEvents(Document eventsDoc) {
            StringBuilder sb = null;
            Element root = eventsDoc.getDocumentElement();

            if (root != null) {
                NodeList eventNodes = root.getElementsByTagNameNS(EVENTS_XML_NS, "event");
                if (eventNodes != null) {
                    sb = new StringBuilder(String.format("\n" +
                            ">================================\n  Events(%d found): \n", eventNodes.getLength()));
                    for (int i = 0; i < eventNodes.getLength(); i++) {
                        Node eventNode = eventNodes.item(i);
                        if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eventElement = (Element) eventNode;
                            sb.append(" o ").append(printEvent(eventElement)).append("\n");
                        }
                    }
                    sb.append("<================================\n");
                }
            }

            return sb;
        }

        /**
         * @param eventElement
         * @return event info
         */
        String printEvent(Element eventElement) {
            StringBuilder elementTextSB = new StringBuilder();
            String categoryCode = "";
            if (eventElement != null) {
                // For category:
                Element categoryElement = DomUtil.loadFirstElementNode(eventElement, "event-category");
                if (categoryElement != null) {
                    categoryCode = categoryElement.getAttribute("code");
                }

                // Build info:
                elementTextSB.append(String.format("EventState:{id='%s', title='%s', created='%s', category='%s', severity='%s'}"
                        , eventElement.getAttribute("id"), DomUtil.loadChildElementText(eventElement, "title")
                        , eventElement.getAttribute("created")
                        , categoryCode, eventElement.getAttribute("severity")));

            }
            return elementTextSB.toString();
        }

    }
}
