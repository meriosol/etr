package com.meriosol.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CAUTION: <code>DateFormat</code> is not thread safe, use with care (consider synchronized or threadlocal).
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class DateUtil {
    public static final DateFormat DEFAULT_FULL_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private DateUtil() {
    }

    public static synchronized Date parseFullDateWithDefaultFormat(String dateString) throws ParseException {
        return dateString != null ? DEFAULT_FULL_DATE_FORMATTER.parse(dateString) : null;
    }

    public static synchronized String formatDateWithDefaultFormat(Date date) throws ParseException {
        return date != null ? DEFAULT_FULL_DATE_FORMATTER.format(date) : "";
    }


}
