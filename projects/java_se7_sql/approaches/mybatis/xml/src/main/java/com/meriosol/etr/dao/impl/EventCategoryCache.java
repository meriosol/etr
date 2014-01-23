package com.meriosol.etr.dao.impl;

import com.meriosol.etr.domain.Event;
import com.meriosol.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Event categories cache.<br>
 * NOTE: for now reload is invisible to client code.<br>
 * TODO: impl mybatis load of data
 *
 * @author meriosol
 * @version 0.1
 * @since 22/01/14
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
     * @param connection
     * @return Category
     * @throws SQLException
     */
    Event.Category lookupCategory(String categoryCode, Connection connection) throws SQLException {

        if (connection == null) {
            throw new IllegalArgumentException("Connection should not be null!");
        }
        if (Util.isObjectNullOrTrimmedEmpty(categoryCode)) {
            throw new IllegalArgumentException("categoryCode should not be empty!");
        }
        reloadListIfEmpty(connection);
        Event.Category category = null;
        if (this.categories != null) {
            category = categories.get(categoryCode);
        }
        return category;
    }

    private void reloadListIfEmpty(Connection connection) throws SQLException {
        if (this.categories == null) {
            try (PreparedStatement retrieveStatement = connection.prepareStatement(CATEGORIES_QUERY)) {
                ResultSet resultSet = retrieveStatement.executeQuery();
                this.categories = new HashMap<>();
                LOG.info("These categories to be added to cache:");
                while (resultSet.next()) {
                    String code = resultSet.getString("code");
                    String name = resultSet.getString("name");
                    Event.Category category = new Event.Category(code, name);
                    categories.put(code, category);
                    LOG.info(" o category: {}", new Object[]{category});
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
