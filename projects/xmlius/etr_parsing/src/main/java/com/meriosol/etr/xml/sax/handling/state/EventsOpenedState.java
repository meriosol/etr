package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.xml.sax.Attributes;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * State for root (opening events).
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EventsOpenedState extends EtrBaseState {
    private static final Class<EventsOpenedState> MODULE = EventsOpenedState.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());

    private EtrInfoKeeper etrInfoKeeper;
    private EventOpenedState eventOpenedState;

    public EventsOpenedState(EtrInfoKeeper etrInfoKeeper) {
        this.etrInfoKeeper = etrInfoKeeper;
    }

    @Override
    public String getStateName() {
        return "EventsOpenedState";
    }

    @Override
    public void handleEventsOpening() {
        initState();
    }

    @Override
    public void handleEventOpening(Properties properties) {
        // NOTE: it's common pattern across code - to create corresponding sub-state and delegate handling to it.
        this.eventOpenedState = new EventOpenedState();
        this.eventOpenedState.handleEventOpening(properties);
    }

    @Override
    public void handleEventCategoryOpening(Properties properties) {
        if (this.eventOpenedState != null) {
            this.eventOpenedState.handleEventCategoryOpening(properties);
        } else {
            lOG.warning(getStateName() + " CAUTION: eventOpenedState is still null, so category state can not be created.");
        }
    }

    @Override
    public void handleEventCategoryClosing() {
        if (this.eventOpenedState != null) {
            this.eventOpenedState.handleEventCategoryClosing();
        }
    }

    @Override
    public void handleEventClosing() {
        if (this.eventOpenedState != null) {
            EventInfo eventInfo = eventOpenedState.getEventInfo();
            if (eventInfo != null) {
                this.etrInfoKeeper.addEventInfo(eventInfo);
            } else {
                lOG.warning(getStateName() + " CAUTION: eventInfo is still null though now we're closing event..");
            }
            this.eventOpenedState = null;
        }
    }

    @Override
    public void handleEventsClosing() {
        cleanupState();
    }

    @Override
    public void handleAddingPropertyKey(String key) {
        if (this.eventOpenedState != null) {
            this.eventOpenedState.handleAddingPropertyKey(key);
        } else {
            setTempPropertyKey(key);
        }
    }

    @Override
    public void handleAddingText(String text) {
        if (this.eventOpenedState != null) {
            this.eventOpenedState.handleAddingText(text);
        } else {
            lOG.warning(String.format("%s CAUTION: because eventOpenedState is still null, text='%s' won't be set.", getStateName(), text));
        }
    }

    /**
     * @return gathered event list
     */
    public List<EventInfo> getEventInfoList() {
        return this.etrInfoKeeper.getEventInfoList();
    }

    private void initState() {
    }

    private void cleanupState() {
        this.eventOpenedState = null;
        this.etrInfoKeeper = null;
        // NOTE: Well, yes, GC does it too :). More for future complex resources release..
    }

}
