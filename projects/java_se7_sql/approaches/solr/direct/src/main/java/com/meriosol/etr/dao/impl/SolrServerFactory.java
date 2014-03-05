package com.meriosol.etr.dao.impl;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author meriosol
 * @version 0.1
 * @since 04/03/14
 */
class SolrServerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SolrServerFactory.class);
    private static SolrServerFactory instance = new SolrServerFactory();
    private SolrServerHolder solrServerHolder;

    static SolrServerFactory getInstance() {
        return instance;
    }

    SolrServer findSolrServer(SolrCoreCode solrCoreCode) {
        return this.solrServerHolder.getSolrServer(solrCoreCode);
    }

    private SolrServerFactory() {
        this.solrServerHolder = new SolrServerHolder();
    }

    private class SolrServerHolder {
        private Map<SolrCoreCode, SolrServer> servers = null;
        private Settings settings;

        private SolrServerHolder() {
            this.settings = new Settings();
            this.servers = new HashMap<>();
        }

        SolrServer getSolrServer(SolrCoreCode solrCoreCode) {
            SolrServer solrServer = this.servers.get(solrCoreCode);
            if (solrServer == null) {
                solrServer = loadSolrServer(solrCoreCode);
                this.servers.put(solrCoreCode, solrServer);
            }
            return solrServer;
        }

        private SolrServer loadSolrServer(SolrCoreCode solrCoreCode) {
            HttpSolrServer httpSolrServer = new HttpSolrServer(this.settings.getSolrCoreUrl(solrCoreCode));

            // defaults to 0.  > 1 not recommended.
            httpSolrServer.setMaxRetries(Integer.parseInt(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.MAX_RETRIES)));

            // 5 seconds to establish TCP
            httpSolrServer.setConnectionTimeout(Integer.parseInt(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.CONNECTION_TIMEOUT)));

            // Setting the XML response parser is only required for cross
            // version compatibility and only when one side is 1.4.1 or
            // earlier and the other side is 3.1 or later.
            // httpSolrServer.setParser(new XMLResponseParser()); // binary parser is used by default
            // The following settings are provided here for completeness.
            // They will not normally be required, and should only be used
            // after consulting javadocs to know whether they are truly required.

            // Socket read timeout
            httpSolrServer.setSoTimeout(Integer.parseInt(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.SOCKET_TIMEOUT)));
            httpSolrServer.setDefaultMaxConnectionsPerHost(Integer.parseInt(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.DEFAULT_MAX_CONNECTIONS_PER_HOST)));
            httpSolrServer.setMaxTotalConnections(Integer.parseInt(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.MAX_TOTAL_CONNECTIONS)));

            // defaults to false
            httpSolrServer.setFollowRedirects(Boolean.getBoolean(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.FOLLOW_REDIRECTS)));
            // allowCompression defaults to false.
            // Server side must support gzip or deflate for this to have any effect.
            httpSolrServer.setAllowCompression(Boolean.getBoolean(this.settings.getDbConfigProperty(Settings.ConfigNames.Server.ALLOW_COMPRESSION)));

            return httpSolrServer;

        }

    }

}
