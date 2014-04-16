package com.meriosol.etr.xml.sax.handling;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.List;

/**
 * Credits: http://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html
 *
 * @author meriosol
 * @version 0.1
 * @since 16/04/14
 */
public class EventsSaxErrorHandler implements ErrorHandler {
    private List<String> parsingMessagesHolder;

    /**
     * @param parsingMessagesHolder Will contain warnings and likely other parsing messages.
     */
    public EventsSaxErrorHandler(List<String> parsingMessagesHolder) {
        this.parsingMessagesHolder = parsingMessagesHolder;
    }

    /**
     * @param spe
     * @throws SAXException
     */
    public void warning(SAXParseException spe) throws SAXException {
        this.parsingMessagesHolder.add("Warning: " + getParseExceptionInfo(spe));
    }

    /**
     * @param spe
     * @throws SAXException
     */
    public void error(SAXParseException spe) throws SAXException {
        String message = "Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    /**
     * @param spe
     * @throws SAXException
     */
    public void fatalError(SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();

        if (systemId == null) {
            systemId = "null";
        }

        String info = "URI=" + systemId + " Line="
                + spe.getLineNumber() + ": " + spe.getMessage();

        return info;
    }

}
