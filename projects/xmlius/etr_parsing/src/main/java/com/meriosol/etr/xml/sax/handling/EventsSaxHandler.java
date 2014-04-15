package com.meriosol.etr.xml.sax.handling;

import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import com.meriosol.etr.xml.sax.handling.state.EtrStateContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.logging.Logger;

/**
 * Basic events data handler. TODO: finish.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventsSaxHandler extends DefaultHandler {
    private static final Class<EventsSaxHandler> MODULE = EventsSaxHandler.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private EtrStateContext etrStateContext;

    /**
     * These elements have states (see GoF State design pattern).
     */
    private interface MajorElementNames {
        String EVENTS = "events";
        String EVENT = "event";
        String EVENT_CATEGORY = "event-category";
    }

    public EventsSaxHandler() {
        this.etrStateContext = new EtrStateContext();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        lOG.info(String.format(" oo startElement: localName='%s', qName='%s', uri='%s'..", localName, qName, uri));
        if (MajorElementNames.EVENTS.equals(qName)) {
            this.etrStateContext.openEvents();
        } else if (MajorElementNames.EVENT.equals(qName)) {
            this.etrStateContext.openEvent(attributes);
        } else if (MajorElementNames.EVENT_CATEGORY.equals(qName)) {
            this.etrStateContext.openEventCategory(attributes);
        } else {
            this.etrStateContext.addPropertyKey(qName);
            lOG.info(String.format(" oo startElement: qName='%s' was added to context / state..", qName));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        lOG.info(String.format(" oo endElement: localName='%s', qName='%s', uri='%s' ", localName, qName, uri));
        if (MajorElementNames.EVENTS.equals(qName)) {
            this.etrStateContext.closeEvents();
        } else if (MajorElementNames.EVENT.equals(qName)) {
            this.etrStateContext.closeEvent();
        } else if (MajorElementNames.EVENT_CATEGORY.equals(qName)) {
            this.etrStateContext.closeEventCategory();
        } else {
            // TODO: finish
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String text = buildString(ch, start, length);
        if (text != null) {
            text = text.trim();
            if (text.length() > 0) {
                lOG.info(" oo characters: " + text);
                this.etrStateContext.addText(text);
            }
        }
    }

    /**
     * @return gathered events info
     */
    public List<EventInfo> getEventInfoList() {
        return this.etrStateContext.getEventInfoList();
    }


    private String buildString(char[] ch, int start, int length) {
        return new String(ch, start, length);
    }
}
