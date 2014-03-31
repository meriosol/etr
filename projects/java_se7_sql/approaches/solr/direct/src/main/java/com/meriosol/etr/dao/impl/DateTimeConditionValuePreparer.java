package com.meriosol.etr.dao.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

/**
 * Util class for creating validly formatted date fields for filter conditions in solr queries.
 *
 * @author meriosol
 * @version 0.1
 * @since 04/03/14
 */
class DateTimeConditionValuePreparer {
    private static final DateTimeFormatter XML_DATE_FORMATTER = ISODateTimeFormat.dateHourMinuteSecondMillis();
    private static final String INFINITE_SIGN = "*";

    private DateTimeConditionValuePreparer() {
    }

    static String prepareDateForCondition(Date date) {
        String result = INFINITE_SIGN;
        if (date != null) {
            DateTime dt = new DateTime(date);
            result = XML_DATE_FORMATTER.withZoneUTC().print(dt) + 'Z';
        }
        return result;
    }

}
