package com.meriosol.etr.xml.stax;

import com.meriosol.etr.CommonUtil;
import com.meriosol.etr.domain.Info;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stax util <br>
 * Useful Links: http://www.vogella.com/tutorials/JavaXML/article.html
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

    //--------------------------------------------
    // For parsing:


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

    /**
     * Element builder for basic elements.
     *
     * @param info
     * @param xmlEventReader
     * @param parentStartElement
     * @throws XMLStreamException
     */
    public static void addChildSimpleElementsDataToInfo(Info info,
                                                        XMLEventReader xmlEventReader, StartElement parentStartElement)
            throws XMLStreamException {
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

    /**
     * Validates XML from <code>xmlEventReader</code> against XSD <code>xsdResourcePath</code>.
     *
     * @param xmlEventReader
     * @param xsdResourcePath
     * @throws SAXException
     * @throws XMLStreamException
     * @throws IOException
     */
    public static void validateAgainstSchema(XMLEventReader xmlEventReader, String xsdResourcePath) throws SAXException, XMLStreamException, IOException {
        validateAgainstSchema(xmlEventReader, CommonUtil.getResourceUrl(xsdResourcePath));
    }

    /**
     * Validates XML from <code>xmlEventReader</code> against XSD <code>xsdResourceUrl</code>.
     *
     * @param xmlEventReader
     * @param xsdResourceUrl
     * @throws SAXException
     * @throws XMLStreamException
     * @throws IOException
     */
    public static void validateAgainstSchema(XMLEventReader xmlEventReader, URL xsdResourceUrl) throws SAXException, XMLStreamException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsdResourceUrl);

        Validator validator = schema.newValidator();
        validator.validate(new StAXSource(xmlEventReader));
    }


    //--------------------------------------------
    // For writing:

    /**
     * @param outputFile
     * @return eventWriter
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public static XMLEventWriter createXMLEventWriterWithStartingDocForWriting(File outputFile) throws FileNotFoundException, XMLStreamException {
        // create an XMLOutputFactory
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        // create XMLEventWriter
        XMLEventWriter eventWriter = outputFactory
                .createXMLEventWriter(new FileOutputStream(outputFile));
        return eventWriter;
    }

    /**
     * Credit: http://www.vogella.com/tutorials/JavaXML/article.html
     *
     * @param eventWriter
     * @param name
     * @param value
     * @param namespaceUri
     * @throws XMLStreamException
     */
    public static void createNode(XMLEventWriter eventWriter, String name,
                                  String value, String namespaceUri) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", namespaceUri, name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);

    }

    /**
     * @param eventFactory
     * @param prefix
     * @param namespaceUri
     * @return Namespaces
     */
    public static Iterator<Namespace> createNamespaces(XMLEventFactory eventFactory, String prefix, String namespaceUri) {
        Namespace namespace = eventFactory.createNamespace(prefix, namespaceUri);
        List<Namespace> namespaces = new ArrayList<>();
        namespaces.add(namespace);
        return namespaces.iterator();
    }

}
