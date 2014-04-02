package com.meriosol.etr.dao.impl;

import com.meriosol.etr.domain.Event;
import com.meriosol.util.Util;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
 * @since 04/03/14
 */
class EventCategoryCache {
    private static final Logger LOG = LoggerFactory.getLogger(EventCategoryCache.class);

    private Map<String, Event.Category> categories = null;

    private static final String CATEGORIES_QUERY = "*:*";

    private static final EventCategoryCache instance = new EventCategoryCache();

    static EventCategoryCache getInstance() {
        return instance;
    }

    private EventCategoryCache() {
    }

    /**
     * @param categoryCode
     * @param solrServerFactory
     * @return Category
     */
    Event.Category lookupCategory(String categoryCode, SolrServerFactory solrServerFactory) throws SolrServerException {
        if (solrServerFactory == null) {
            throw new IllegalArgumentException("SolrServerFactory should not be null!");
        }
        if (Util.isObjectNullOrTrimmedEmpty(categoryCode)) {
            throw new IllegalArgumentException("categoryCode should not be empty!");
        }
        reloadListIfEmpty(solrServerFactory);
        Event.Category category = null;
        if (this.categories != null) {
            category = categories.get(categoryCode);
        }
        return category;
    }

    private void reloadListIfEmpty(SolrServerFactory solrServerFactory) throws SolrServerException {
        if (this.categories == null) {
            SolrServer solrServer = solrServerFactory.findSolrServer(SolrCoreCode.EVENT_CATEGORIES);
            if (solrServer != null) {
                SolrQuery solrQuery = new SolrQuery();
                solrQuery.set("q", CATEGORIES_QUERY);
                QueryResponse response = solrServer.query(solrQuery);
                if (response != null) {
                    SolrDocumentList results = response.getResults();
                    if (results != null) {
                        LOG.debug("o Categories:");
                        this.categories = new HashMap<>();
                        for (SolrDocument solrDocument : results) {
                            Object code = solrDocument.getFieldValue("code");
                            Object name = solrDocument.getFieldValue("name");
                            LOG.debug(" o Category: code='{}', name='{}'", code, name);
                            Event.Category category = new Event.Category(code.toString(), name.toString());
                            this.categories.put(code.toString(), category);
                        }

                    } else {
                        LOG.error("SolrDocumentList results is null!");
                    }
                } else {
                    LOG.error("QueryResponse is null!");
                }
            } else {
                LOG.error("SolrServer was not created!");
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
