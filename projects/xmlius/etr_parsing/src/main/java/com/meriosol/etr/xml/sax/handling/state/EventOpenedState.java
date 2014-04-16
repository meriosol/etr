package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.xml.sax.Attributes;

import java.util.Properties;

/**
 * State for particular opened event.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventOpenedState extends EtrBaseState {
    private EventInfo eventInfo;
    private EventCategoryOpenedState eventCategoryOpenedState;

    @Override
    public String getStateName() {
        return "EventOpenedState";
    }

    @Override
    public void handleEventsOpening() {
        // ignore
    }

    @Override
    public void handleEventOpening(Properties properties) {
        initState(properties);
    }

    @Override
    public void handleEventCategoryOpening(Properties properties) {
        // NOTE: it's common pattern across code - to create corresponding sub-state and delegate handling to it.
        this.eventCategoryOpenedState = new EventCategoryOpenedState();
        this.eventCategoryOpenedState.handleEventCategoryOpening(properties);
    }

    @Override
    public void handleEventCategoryClosing() {
        if (this.eventCategoryOpenedState != null) {
            eventInfo.setEventCategory(this.eventCategoryOpenedState.getEventCategoryInfo());
        }
        this.eventCategoryOpenedState = null;
    }

    @Override
    public void handleEventClosing() {
        // NOTE: potentially eventInfo could be added in context event list.
        cleanupState();
    }

    @Override
    public void handleEventsClosing() {
        // ignore
    }

    @Override
    public void handleAddingPropertyKey(String key) {
        if (this.eventCategoryOpenedState != null) {
            this.eventCategoryOpenedState.handleAddingPropertyKey(key);
        } else if (this.eventInfo != null) {
            setTempPropertyKey(key);
        }
    }

    @Override
    public void handleAddingText(String text) {
        // NOTE: If category is opened hence property is for it, not for event.
        if (this.eventCategoryOpenedState != null) {
            this.eventCategoryOpenedState.handleAddingText(text);
        } else if (this.eventInfo != null) {
            addProperty(this.eventInfo, text);
        }
    }

    /**
     * @return event info
     */
    public EventInfo getEventInfo() {
        return eventInfo;
    }

    private void initState(Properties properties) {
        this.eventInfo = new EventInfo(properties);
    }

    private void cleanupState() {
        this.eventInfo = null;
        this.eventCategoryOpenedState = null;
    }

}
