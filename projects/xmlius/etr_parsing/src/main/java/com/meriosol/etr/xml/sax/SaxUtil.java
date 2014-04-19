package com.meriosol.etr.xml.sax;

import com.meriosol.etr.CommonUtil;
import com.meriosol.etr.domain.EventInfo;
import com.meriosol.etr.xml.sax.handling.EventsSaxContentHandler;
import com.meriosol.etr.xml.sax.handling.EventsSaxErrorHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * SAX utils.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class SaxUtil {
    private static final Class<SaxUtil> MODULE = SaxUtil.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String DEFAULT_ENCODING = "UTF-8";

    private interface ValidatorConstants {
        String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
        String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    }

    private SaxUtil() {
    }

    /**
     * @param eventsResourcePath
     * @return event list
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static List<EventInfo> gatherEventInfo(String eventsResourcePath)
            throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = buildSAXParser();
        URL eventsResourceUrl = CommonUtil.getResourceUrl(eventsResourcePath);

        EventsSaxContentHandler eventsSaxContentHandler = new EventsSaxContentHandler();

        Reader reader = new InputStreamReader(eventsResourceUrl.openStream(), DEFAULT_ENCODING);
        InputSource is = new InputSource(reader);
        is.setEncoding(DEFAULT_ENCODING);

        saxParser.parse(is, eventsSaxContentHandler);

        return eventsSaxContentHandler.getEventInfoList();
    }

    /**
     * @param eventsResourcePath
     * @param schemaResourcePath
     * @param parsingMessagesHolder Will contain messages gathered while parsing XML
     * @param eventsResourcePath
     * @return event list
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static List<EventInfo> gatherEventInfo(String eventsResourcePath, String schemaResourcePath
            , List<String> parsingMessagesHolder)
            throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = buildSAXParser();
        saxParser.setProperty(ValidatorConstants.JAXP_SCHEMA_LANGUAGE, ValidatorConstants.W3C_XML_SCHEMA);

        URL schemaResourceUrl = CommonUtil.getResourceUrl(schemaResourcePath);
        saxParser.setProperty(ValidatorConstants.JAXP_SCHEMA_SOURCE, schemaResourceUrl.openStream());

        // Content handler:
        EventsSaxContentHandler eventsSaxContentHandler = new EventsSaxContentHandler();

        // Error handler:
        EventsSaxErrorHandler eventsSaxErrorHandler = new EventsSaxErrorHandler(parsingMessagesHolder);

        // XML reader:
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(eventsSaxContentHandler);
        xmlReader.setErrorHandler(eventsSaxErrorHandler);

        // Convert to URL:
        URL eventsResourceUrl = CommonUtil.getResourceUrl(eventsResourcePath);

        // Input source
        Reader reader = new InputStreamReader(eventsResourceUrl.openStream(), DEFAULT_ENCODING);
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding(DEFAULT_ENCODING);

        // Parse:
        xmlReader.parse(inputSource);

        return eventsSaxContentHandler.getEventInfoList();
    }

    /**
     * @param attributes
     * @return gathered info about attributes
     */
    public static String gatherAttributesInfo(Attributes attributes) {
        StringBuilder sb = new StringBuilder("Attrs: {");
        if (attributes != null) {
            int ln = attributes.getLength();
            for (int i = 0; i < ln; i++) {
                sb.append(" ").append(attributes.getQName(i)).append("='").append(attributes.getValue(i)).append("'");
                if (i < attributes.getLength() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        }
        return sb.toString();
    }

    /**
     * @param attributes
     * @return converted attrib-s
     */
    public static Properties convertAttributesToProps(Attributes attributes) {
        Properties properties = new Properties();
        if (attributes != null) {
            int ln = attributes.getLength();
            for (int i = 0; i < ln; i++) {
                properties.setProperty(attributes.getQName(i), attributes.getValue(i));
            }
        }
        return properties;
    }

    public static SAXParser buildSAXParser() throws ParserConfigurationException, SAXException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        saxParserFactory.setValidating(true);

        SAXParser saxParser = saxParserFactory.newSAXParser();
        try {
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "file, jar:file");
        } catch (IllegalArgumentException e) {
            //jaxp 1.5 feature not supported
            lOG.warning("Looks like jaxp 1.5 feature not supported: " + e.getMessage());
        }
        return saxParser;
    }


}
