package com.meriosol.etr.dao.impl;

import com.meriosol.util.PropertiesLoader;
import com.meriosol.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads(lazily) and keeps DB connection properties.
 *
 * @author meriosol
 * @version 0.1
 * @since 04/03/14
 */
class Settings {
    private static final String DB_PROPERTIES_PATH = "/db.properties";
    private Properties dbConfigStorage = null;

    private static Map<SolrCoreCode, String> solrCoreNameMap;

    static {
        fillSolrCoreNameMap();
    }

    private static void fillSolrCoreNameMap() {
        solrCoreNameMap = new HashMap<>();
        solrCoreNameMap.put(SolrCoreCode.EVENT_CATEGORIES, ConfigNames.Core.EVENT_CATEGORIES);
        solrCoreNameMap.put(SolrCoreCode.EVENTS, ConfigNames.Core.EVENTS);
    }

    interface ConfigNames {
        String PREFIX = "solr";
        String URL = PREFIX + ".url";

        interface Server {
            String SERVER_PREFIX = PREFIX + ".server";

            String MAX_RETRIES = SERVER_PREFIX + ".MaxRetries";
            String CONNECTION_TIMEOUT = SERVER_PREFIX + ".ConnectionTimeout";
            String SOCKET_TIMEOUT = SERVER_PREFIX + ".SoTimeout";
            String DEFAULT_MAX_CONNECTIONS_PER_HOST = SERVER_PREFIX + ".DefaultMaxConnectionsPerHost";
            String MAX_TOTAL_CONNECTIONS = SERVER_PREFIX + ".MaxTotalConnections";
            String FOLLOW_REDIRECTS = SERVER_PREFIX + ".FollowRedirects";
            String ALLOW_COMPRESSION = SERVER_PREFIX + ".AllowCompression";
        }

        interface Core {
            String CORE_PREFIX = PREFIX + ".core";
            // NOTE: add new core here once time comes.
            String EVENT_CATEGORIES = CORE_PREFIX + ".event_categories";
            String EVENTS = CORE_PREFIX + ".events";
        }
    }


    Settings() {
    }

    String getSolrCoreUrl(SolrCoreCode solrCoreCode) {
        return getDbConfigProperty(ConfigNames.URL) + "/" + getSolrCorePropertyValue(solrCoreCode);
    }

    String getDbConfigProperty(String propertyName) {
        Properties dbConfigProperties = loadDbConfigProperties();
        String dbConfigProperty = "";
        if (dbConfigProperties != null) {
            dbConfigProperty = Util.trimIfNotNull(dbConfigProperties.getProperty(propertyName));
        }
        return dbConfigProperty;
    }

    private static String getSolrCorePropertyName(SolrCoreCode solrCoreCode) {
       return solrCoreNameMap.get(solrCoreCode);
    }

    private String getSolrCorePropertyValue(SolrCoreCode solrCoreCode) {
        return getDbConfigProperty(getSolrCorePropertyName(solrCoreCode));
    }


    /**
     * If dynamic reloading of settings required, this method can be used.<br>
     * TODO: consider avoiding external components handle this internal matter.
     *
     * @return true if config is not null after reload.
     */
    boolean enforceReloadDbConfigProperties() {
        this.dbConfigStorage = PropertiesLoader.loadPropertiesFromClassPath(DB_PROPERTIES_PATH);
        return this.dbConfigStorage != null;
    }

    private Properties loadDbConfigProperties() {
        if (this.dbConfigStorage == null) {
            enforceReloadDbConfigProperties();
        }
        return this.dbConfigStorage;
    }



}
