package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.xml.sax.handling.domain.EventInfo;
import org.xml.sax.Attributes;

import java.util.List;

/**
 * State context.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class EtrStateContext {
    private EtrBaseState state;
    private EtrStateFactory etrStateFactory;
    private EtrInfoKeeper etrInfoKeeper;

    public EtrStateContext() {
        this.etrStateFactory = new EtrStateFactory();
        this.etrInfoKeeper = new EtrInfoKeeper();
        this.state = this.etrStateFactory.buildStartingState(etrInfoKeeper);
    }

    // NOTE: yes, it's a bit artificial to name state methods differently here. It's done to separate semantically state form context.

    //--------------
    // Openings:
    public void openEvents() {
        this.state.handleEventsOpening();
    }

    public void openEvent(Attributes attributes) {
        this.state.handleEventOpening(attributes);
    }

    public void openEventCategory(Attributes attributes) {
        this.state.handleEventCategoryOpening(attributes);
    }

    //--------------
    // Closings:
    public void closeEventCategory() {
        this.state.handleEventCategoryClosing();
    }

    public void closeEvent() {
        this.state.handleEventClosing();

    }

    public void closeEvents() {
        this.state.handleEventsClosing();
    }

    //--------------
    // Misc:
    public void addPropertyKey(String key) {
        this.state.handleAddingPropertyKey(key);
    }

    public void addText(String text) {
        this.state.handleAddingText(text);
    }

    /**
     * @return gathered events info
     */
    public List<EventInfo> getEventInfoList() {
        return this.etrInfoKeeper.getEventInfoList();
    }


}
