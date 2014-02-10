package com.meriosol.etr.dao.impl;

import com.meriosol.util.PropertiesLoader;
import com.meriosol.util.Util;

import java.util.Properties;

/**
 * Loads(lazily) and keeps DB connection properties.
 *
 * @author meriosol
 * @version 0.1
 * @since 09/02/14
 */
class DbSessionSettingsHolder {
    private static final String DB_PROPERTIES_PATH = "/db.properties";
    private Properties dbConfigStorage = null;

    private interface ConfigNames {
        String PREFIX = "cassandra";

        interface Cql {
            String HOSTS = PREFIX + ".hosts";
            String KEYSPACE = PREFIX + ".keyspace";
            String USERNAME = PREFIX + ".username";
            String PASSWORD = PREFIX + ".password";
        }
    }


    DbSessionSettingsHolder() {
    }

    public String getHosts() {
        return getDbConfigProperty(ConfigNames.Cql.HOSTS);
    }

    public String getKeyspace() {
        return getDbConfigProperty(ConfigNames.Cql.KEYSPACE);
    }
    public String getUsername() {
        return getDbConfigProperty(ConfigNames.Cql.USERNAME);
    }
    public String getPassword() {
        return getDbConfigProperty(ConfigNames.Cql.PASSWORD);
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

    private String getDbConfigProperty(String propertyName) {
        Properties dbConfigProperties = loadDbConfigProperties();
        String dbConfigProperty = "";
        if (dbConfigProperties != null) {
            dbConfigProperty = Util.trimIfNotNull(dbConfigProperties.getProperty(propertyName));
        }
        return dbConfigProperty;
    }


}
