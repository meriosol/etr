package com.meriosol.etr.xml.stax;

import com.meriosol.etr.CommonUtil;
import com.meriosol.etr.domain.Info;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Stax util <br>
 * Links: http://www.vogella.com/tutorials/JavaXML/article.html
 *
 * @author meriosol
 * @version 0.1
 * @since 16/04/14
 */
public class StaxUtil {
    private static final Class<StaxUtil> MODULE = StaxUtil.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());

    private StaxUtil() {
    }

    /**
     * @param xmlResourcePath
     * @return Event reader, ready for iterating across XML events.
     * @throws IOException
     * @throws XMLStreamException
     */
    public static XMLEventReader createXMLEventReaderForResource(String xmlResourcePath) throws IOException, XMLStreamException {
        return createXMLEventReader(CommonUtil.getResourceUrl(xmlResourcePath));
    }

    /**
     * @param xmlResourceUrl
     * @return Event reader, ready for iterating across XML events.
     * @throws IOException
     * @throws XMLStreamException
     */
    public static XMLEventReader createXMLEventReader(URL xmlResourceUrl) throws IOException, XMLStreamException {
        XMLEventReader eventReader = null;
        if (xmlResourceUrl != null) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(xmlResourceUrl.openStream());
        }
        return eventReader;
    }

    /**
     * @param info
     * @param attributes
     */
    public static void addAttributesToInfo(Info info, Iterator<Attribute> attributes) {
        if (info != null && attributes != null) {
            while (attributes.hasNext()) {
                Attribute attribute = attributes.next();
                info.addProperty(attribute.getName().toString(), attribute.getValue());
            }
        }
    }

    public static void addChildSimpleElementsDataToInfo(Info info, XMLEventReader xmlEventReader, StartElement parentStartElement) throws XMLStreamException {
        if (info != null && xmlEventReader != null && parentStartElement != null) {
            // Goal: iterate events unless end element with the same name
            // as parent is not achieved and add all data from child elements to info POJO properties.
            String parentElementName = parentStartElement.getName().getLocalPart();

            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();
                    addChildSimpleElementDataToInfo(info, xmlEventReader, elementName);
                } else if (event.isEndElement()) {
                    // If we reach the end of an item element, we add it to the list
                    EndElement endElement = event.asEndElement();
                    if (parentElementName.equals(endElement.getName().getLocalPart())) {
                        break;
                    }
                }
            }
        }
    }


    /**
     * Adds data from next event of simple element with name <code>elementName</code> as a code into <code>info</code> property.
     *
     * @param info           Info POJO.
     * @param xmlEventReader Used to traverse into the next event (which is expected to be Char array).
     * @param elementName    Simple (no child elements expected) element name.
     * @throws XMLStreamException
     */
    public static void addChildSimpleElementDataToInfo(Info info, XMLEventReader xmlEventReader, String elementName) throws XMLStreamException {
        if (info != null && xmlEventReader != null && elementName != null) {
            XMLEvent childEvent = xmlEventReader.nextEvent();
            if (childEvent != null) {
                // NOTE: assuming non category events are simple nodes..
                Characters characters = childEvent.asCharacters();
                if (characters != null) {
                    info.addProperty(elementName, characters.getData());
                }
            }
        }
    }
}
