package com.meriosol.etr.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.meriosol.etr.domain.Event;
import com.meriosol.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Event categories cache.<br>
 * NOTE: for now reload is invisible to client code.
 *
 * @author meriosol
 * @version 0.1
 * @since 09/02/14
 */
class EventCategoryCache {
    private static final Logger LOG = LoggerFactory.getLogger(EventCategoryCache.class);

    private Map<String, Event.Category> categories = null;
    private static final String CATEGORIES_QUERY = "select code, name from event_categories";

    private static EventCategoryCache instance = new EventCategoryCache();

    static EventCategoryCache getInstance() {
        return instance;
    }

    private EventCategoryCache() {
    }

    /**
     * @param categoryCode
     * @param session
     * @param keyspace
     * @return Category
     */
    Event.Category lookupCategory(String categoryCode, Session session, String keyspace) {
        if (session == null) {
            throw new IllegalArgumentException("Session should not be null!");
        }
        if (Util.isObjectNullOrTrimmedEmpty(categoryCode)) {
            throw new IllegalArgumentException("categoryCode should not be empty!");
        }
        reloadListIfEmpty(session, keyspace);
        Event.Category category = null;
        if (this.categories != null) {
            category = categories.get(categoryCode);
        }
        return category;
    }

    private void reloadListIfEmpty(Session session, String keyspace) {
        if (this.categories == null) {
            String selector = "SELECT code,name FROM " + keyspace + ".event_categories;";
            LOG.info("BEFORE Retrieve event categories. Selector: {}", selector);
            ResultSet results = session.execute(selector);
            if (results != null) {
                this.categories = new HashMap<>();
                for (Row row : results) {
                    String code = row.getString("code");
                    String name = row.getString("name");
                    Event.Category category = new Event.Category(code, name);
                    this.categories.put(code, category);
                }
            }
        }
    }

    /**
     * @throws Throwable the {@code Exception} raised by this method
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // NOTE: Well, a bit redundant, but let's remain it...
        if (this.categories != null) {
            this.categories.clear();
            this.categories = null;
        }
    }
}
