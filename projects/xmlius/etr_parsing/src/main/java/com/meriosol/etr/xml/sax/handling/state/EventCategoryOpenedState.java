package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.xml.sax.handling.domain.EventCategoryInfo;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * State for particular opened event category.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventCategoryOpenedState extends EtrBaseState {
    private static final Class<EventCategoryOpenedState> MODULE = EventCategoryOpenedState.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
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
    public void handleEventOpening(Properties properties) {
        // ignore
    }

    @Override
    public void handleEventCategoryOpening(Properties properties) {
        initState(properties);
    }

    @Override
    public void handleEventCategoryClosing() {
        cleanupState();
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
        } else {
            lOG.warning(String.format("%s / handleAddingPropertyKey: eventCategoryInfo was not created yet, so key '%s' won't be added.", getStateName(), key));
        }
    }

    @Override
    public void handleAddingText(String text) {
        // NOTE: If category is opened hence property is for it, not for event.
        if (this.eventCategoryInfo != null) {
            addProperty(this.eventCategoryInfo, text);
        } else {
            lOG.warning(String.format("%s / handleAddingText: eventCategoryInfo was not created yet, so text value '%s' won't be added.", getStateName(), text));
        }
    }

    /**
     * @return gathered category info
     */
    public EventCategoryInfo getEventCategoryInfo() {
        return eventCategoryInfo;
    }

    private void initState(Properties properties) {
        this.eventCategoryInfo = new EventCategoryInfo(properties);
    }

    private void cleanupState() {
        this.eventCategoryInfo = null;
    }

}
