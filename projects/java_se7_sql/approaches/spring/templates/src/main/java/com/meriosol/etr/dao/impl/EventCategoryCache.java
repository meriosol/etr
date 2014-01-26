package com.meriosol.etr.dao.impl;

import com.meriosol.etr.domain.Event;
import com.meriosol.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event categories cache.<br>
 * NOTE: for now reload is invisible to client code.
 *
 * @author meriosol
 * @version 0.1
 * @since 18/01/14
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
     * @return Category
     * @throws java.sql.SQLException
     */
    Event.Category lookupCategory(String categoryCode, JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("jdbcTemplate should not be null!");
        }
        if (Util.isObjectNullOrTrimmedEmpty(categoryCode)) {
            throw new IllegalArgumentException("categoryCode should not be empty!");
        }
        reloadListIfEmpty(jdbcTemplate);
        Event.Category category = null;
        if (this.categories != null) {
            category = categories.get(categoryCode);
        }
        return category;
    }

    private void reloadListIfEmpty(JdbcTemplate jdbcTemplate) {
        if (this.categories == null) {
            List<Event.Category> categoryList = jdbcTemplate.query(CATEGORIES_QUERY, new EventCategoryMapper());
            if (categoryList != null && categoryList.size() > 0) {
                this.categories = new HashMap<>();
                for (Event.Category category : categoryList) {
                    this.categories.put(category.getCode(), category);
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

    //------------------------------------
    // Supplement classes/methods:

    /**
     * Position JDBC-POJO mapper.
     */
    private static final class EventCategoryMapper implements RowMapper<Event.Category> {
        public Event.Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event.Category category = new Event.Category();
            category.setCode(rs.getString("code"));
            category.setName(rs.getString("name"));
            return category;
        }
    }
}
