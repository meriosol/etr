package com.meriosol.etr.xml.sax;

import com.meriosol.etr.xml.sax.handling.EventsSaxHandler;
import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class SaxUtil {
    private static final Class<SaxUtil> MODULE = SaxUtil.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String DEFAULT_ENCODING = "UTF-8";
    private SaxUtil() {
    }

    /**
     *
     * @param eventsResourcePath
     * @return event list
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static List<EventInfo> gatherEventInfo(String eventsResourcePath) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = buildSAXParser();
        URL eventsResourceUrl = MODULE.getClassLoader().getResource(eventsResourcePath);

        EventsSaxHandler eventsSaxHandler = new EventsSaxHandler();

        Reader reader = new InputStreamReader(eventsResourceUrl.openStream(),DEFAULT_ENCODING);
        InputSource is = new InputSource(reader);
        is.setEncoding(DEFAULT_ENCODING);
        saxParser.parse(is, eventsSaxHandler);

        return eventsSaxHandler.getEventInfoList();
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
                if (i < attributes.getLength()-1) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        }
        return sb.toString();
    }

    /**
     *
     * @param attributes
     * @return converted attrib-s
     */
    public static Properties convertAttributesToProps(Attributes attributes) {
        Properties properties = new Properties();
        if (attributes != null) {
            int ln = attributes.getLength();
            for (int i = 0; i < ln; i++) {
                properties.setProperty(attributes.getQName(i),attributes.getValue(i));
            }
        }
        return properties;
    }

    public static SAXParser buildSAXParser() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        try {
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "file, jar:file");
        } catch (IllegalArgumentException e) {
            //jaxp 1.5 feature not supported
            lOG.warning("Looks like jaxp 1.5 feature not supported: " + e.getMessage());
        }
        return saxParser;
    }


}
