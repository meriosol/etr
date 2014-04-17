package com.meriosol.etr;

import com.meriosol.etr.domain.EventInfo;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class CommonUtil {
    private static final Class<CommonUtil> MODULE = CommonUtil.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());

    private CommonUtil() {
    }

    /**
     *
     * @param resourcePath
     * @return URL of <code>resourcePath</code>
     */
    public static URL getResourceUrl(String resourcePath) {
        URL eventsResourceUrl = null;
        if (resourcePath != null) {
            eventsResourceUrl =   MODULE.getClassLoader().getResource(resourcePath);
        }
        return eventsResourceUrl;
    }

    /**
     * Logs gathered event data.
     * @param title
     * @param eventInfoList
     */
    public static void logEventsData(String title, List<EventInfo> eventInfoList) {
        String delimLine = "\n~~~ ======================================\n";
        lOG.info(String.format("%s~~~ (%s) Events(%s found): ", delimLine, title, eventInfoList.size()));
        for (EventInfo eventInfo : eventInfoList) {
            lOG.info("~~~~~ event: " + eventInfo);
        }
        lOG.info(delimLine);
    }

}
