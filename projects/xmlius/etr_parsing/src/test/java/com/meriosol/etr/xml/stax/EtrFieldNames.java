package com.meriosol.etr.xml.stax;

/**
* Created by neos on 4/17/2014.
*/
interface EtrFieldNames {
    String EVENTS = "events";
    String EVENT = "event";
    String EVENT_CATEGORY = "event-category";
    interface Event {
        String ID = "id";
        String TITLE = "title";
        String SOURCE = "source";
        String CREATED = "created";

        interface Category {
            String CODE = "code";
            String NAME = "name";
        }
    }
}
