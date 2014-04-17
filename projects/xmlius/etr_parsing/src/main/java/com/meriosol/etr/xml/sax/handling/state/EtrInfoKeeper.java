package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.domain.EventInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps ETR info while gathering data. Added for reducing coupling and cycle dependencies between context and states.
 */
public class EtrInfoKeeper {
    private List<EventInfo> eventInfoList;

    public EtrInfoKeeper() {
        this.eventInfoList = new ArrayList<>();
    }

    /**
     * @return immutable event list
     */
    public List<EventInfo> getEventInfoList() {
        return Collections.unmodifiableList(eventInfoList);
    }

    public void addEventInfo(EventInfo eventInfo) {
        if (eventInfo != null) {
            this.eventInfoList.add(eventInfo);
        }
    }

    /**
     * Cleans underlying collections from data.
     */
    public void clearInfo() {
        this.eventInfoList.clear();
    }
}
