package com.meriosol.etr.xml.xpath;

import com.meriosol.etr.xml.dom.DomUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Sample of how Xpath can be used to filter XML DOM tree.<br>
 * Useful links: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 *
 * @author meriosol
 * @version 0.1
 * @since 18/04/14
 */
public class EventDomXpathTest {
    private static final Class<EventDomXpathTest> MODULE = EventDomXpathTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    // NOTE: EVENTS_XML_NS can be loaded from external config.
    private static final String EVENTS_XML_NS = "http://com/meriosol/etr/schema";
    private EventsPrinter eventsPrinter;
    private EtrNamespaceContext etrNamespaceContext;

    @Before
    public void setUp() {
        this.eventsPrinter = new EventsPrinter();
        this.etrNamespaceContext = new EtrNamespaceContext();
    }

    @After
    public void tearDown() throws Exception {
        // NOTE: well, JVM does it itself(but in more complex cases it's not bad idea to cleanup resources)...
        this.eventsPrinter = null;
        this.etrNamespaceContext = null;
    }


    @Test
    public void testCorrectEventsQuery() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        String eventsResourcePath = "events.xml";
        Document eventsDoc = DomUtil.loadDom(eventsResourcePath);
        assertNotNull(String.format("Sample events DOM tree doc should not be null for resource path '%s'!", eventsResourcePath), eventsDoc);
        eventsDoc.getDocumentElement().normalize();
        lOG.info("~~~ Sample events DOM tree root element: " + eventsDoc.getDocumentElement().getNodeName());
        lOG.info(String.format("Events: \n %s", this.eventsPrinter.printEvents(eventsDoc)));

        // Now let's filter data using xpath:
        // create an XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance();

        // Create an XPath object:
        XPath xpath = xFactory.newXPath();
        xpath.setNamespaceContext(this.etrNamespaceContext);

        XPathExpression xPathExpression = null;
        //------------------------

        // Compile the XPath expression:
        String xpathForWarns = "//ev:event[@severity='WARN']";
        // NOTE: ev prefix maybe sh go to constants..

        xPathExpression = xpath.compile(xpathForWarns);
        // Evaluate compiled expression and get a node set:
        Object result = xPathExpression.evaluate(eventsDoc, XPathConstants.NODESET);

        // Cast the result to a DOM NodeList
        if (result instanceof NodeList) {
            NodeList nodes = (NodeList) result;
            String eventsInfo = this.eventsPrinter.printEvents(nodes);
            lOG.info(String.format("For xpath [[ %s ]] events: \n %s", xpathForWarns, eventsInfo));
        }

        //------------------------
        // Get the number of warnings in events:
        String xpathForEventCount = "count(//ev:event[@severity='WARN'])";
        xPathExpression = xpath.compile(xpathForEventCount);
        Double eventCount = (Double) xPathExpression.evaluate(eventsDoc, XPathConstants.NUMBER);
        lOG.info(String.format("For xpath [[ %s ]] event count = %s.", xpathForEventCount, eventCount));
        //------------------------
    }


    //----------------------------------------------
    // Utils

    /**
     * Namespace Context impl with no prefix(default) namespaces handling.<br>
     * Credits and ideas from e.g.: http://www.edankert.com/defaultnamespaces.html, http://www.ibm.com/developerworks/library/x-nmspccontext/
     */
    private class EtrNamespaceContext implements NamespaceContext {
        public String getNamespaceURI(String prefix) {
            if (prefix == null) throw new NullPointerException("Null prefix");
            else if ("ev".equals(prefix)) return EVENTS_XML_NS;
            else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
            return XMLConstants.NULL_NS_URI;
        }

        // This method isn't necessary for XPath processing.
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        // This method isn't necessary for XPath processing either.
        public Iterator getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Basic events data printer
     */
    private class EventsPrinter {
        String printEvents(NodeList eventNodes) {
            StringBuilder sb = new StringBuilder("");
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
            return sb.toString();
        }

        /**
         * @param eventsDoc
         * @return events info
         */
        StringBuilder printEvents(Document eventsDoc) {
            StringBuilder sb = new StringBuilder("");
            Element root = eventsDoc.getDocumentElement();

            if (root != null) {
                NodeList eventNodes = root.getElementsByTagNameNS(EVENTS_XML_NS, "event");
                if (eventNodes != null) {
                    String printedEvents = printEvents(eventNodes);
                    sb.append(printedEvents);
                } else {
                    lOG.warning("No events found.");
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
