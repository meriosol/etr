package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.xml.sax.handling.domain.EventCategoryInfo;
import org.xml.sax.Attributes;

/**
 * State for particular opened event category.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventCategoryOpenedState extends EtrBaseState {
    private EventCategoryInfo eventCategoryInfo;

    @Override
    public String getStateName() {
        return "EventCategoryOpenedState";
    }

    @Override
    public void handleEventsOpening() {
        // ignore
    }

    @Override
    public void handleEventOpening(Attributes attributes) {
        // ignore
    }

    @Override
    public void handleEventCategoryOpening(Attributes attributes) {
        initState(attributes);
    }

    @Override
    public void handleEventCategoryClosing() {

    }

    @Override
    public void handleEventClosing() {
        // ignore
    }

    @Override
    public void handleEventsClosing() {
        // ignore
    }

    @Override
    public void handleAddingPropertyKey(String key) {
        if (this.eventCategoryInfo != null) {
            setTempPropertyKey(key);
        }
    }

    @Override
    public void handleAddingText(String text) {
        // NOTE: If category is opened hence property is for it, not for event.
        if (this.eventCategoryInfo != null) {
            addProperty(this.eventCategoryInfo, text);
        }
    }

    /**
     * @return gathered category info
     */
    public EventCategoryInfo getEventCategoryInfo() {
        return eventCategoryInfo;
    }

    private void initState(Attributes attributes) {
        this.eventCategoryInfo = new EventCategoryInfo(attributes);
    }

    private void cleanupState() {
        this.eventCategoryInfo = null;
    }

}
