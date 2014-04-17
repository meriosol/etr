package com.meriosol.etr.xml.sax.handling;

import com.meriosol.etr.xml.sax.SaxUtil;
import com.meriosol.etr.domain.EventInfo;
import com.meriosol.etr.xml.sax.handling.state.EtrStateContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Basic events data handler. TODO: finish.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventsSaxContentHandler extends DefaultHandler {
    private static final Class<EventsSaxContentHandler> MODULE = EventsSaxContentHandler.class;
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

    public EventsSaxContentHandler() {
        this.etrStateContext = new EtrStateContext();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        lOG.fine(String.format(" oo startElement: localName='%s', qName='%s', uri='%s', attrib-s: [%s].."
                , localName, qName, uri, SaxUtil.gatherAttributesInfo(attributes)));
        Properties properties = SaxUtil.convertAttributesToProps(attributes);
        if (MajorElementNames.EVENTS.equals(qName)) {
            this.etrStateContext.openEvents();
        } else if (MajorElementNames.EVENT.equals(qName)) {
            this.etrStateContext.openEvent(properties);
        } else if (MajorElementNames.EVENT_CATEGORY.equals(qName)) {
            this.etrStateContext.openEventCategory(properties);
        } else {
            this.etrStateContext.addPropertyKey(qName);
            lOG.info(String.format(" oo startElement: qName='%s' was added to context / state..", qName));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        lOG.fine(String.format(" oo endElement: localName='%s', qName='%s', uri='%s' ", localName, qName, uri));
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
                lOG.fine(" oo characters: " + text);
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
