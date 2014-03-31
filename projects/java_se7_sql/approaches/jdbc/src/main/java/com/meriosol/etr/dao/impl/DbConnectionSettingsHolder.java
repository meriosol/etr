package com.meriosol.etr.dao.impl;

import com.meriosol.util.PropertiesLoader;
import com.meriosol.util.Util;

import java.util.Properties;

/**
 * Loads(lazily) and keeps DB connection properties.
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
class DbConnectionSettingsHolder {
    private static final String DB_PROPERTIES_PATH = "/db.properties";
    private Properties dbSettings = null;
    private Properties dbConfigStorage = null;

    private interface ConfigNames {
        String PREFIX = "jdbc";

        interface Jdbc {
            String DRIVER = PREFIX + ".driver";
            String URL = PREFIX + ".url";
            String USERNAME = PREFIX + ".username";
            String PASSWORD = PREFIX + ".password";
        }

        String USERNAME = "user";
        String PASSWORD = "password";
    }

    DbConnectionSettingsHolder() {
    }

    public String getDbDriverClassName() {
        return getDbConfigProperty(ConfigNames.Jdbc.DRIVER);
    }

    public String getDbUrl() {
        return getDbConfigProperty(ConfigNames.Jdbc.URL);
    }

    public Properties getDbSettings() {
        if (this.dbSettings == null) {
            this.dbSettings = new Properties();
            this.dbSettings.put(ConfigNames.USERNAME, getDbConfigProperty(ConfigNames.Jdbc.USERNAME));
            this.dbSettings.put(ConfigNames.PASSWORD, getDbConfigProperty(ConfigNames.Jdbc.PASSWORD));
        }

        return this.dbSettings;
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
