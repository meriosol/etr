package com.meriosol.etr.xml.stax;

/**
*
 * @author meriosol
 * @version 0.1
 * @since 17/04/14
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
