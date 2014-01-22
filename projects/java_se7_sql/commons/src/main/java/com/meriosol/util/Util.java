package com.meriosol.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Utilities collection class. Contains highly reusable code across modules.<br>
 * TODO: consider using commons-lang utils.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class Util {
    /**
     * This value is to be used as default delimiter for methods where it's not
     * provided.
     */
    public static final String DEFAULT_COLLECTION_DELIMITER = ",";
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";

    private Util() {
    }

    /**
     * Joins elements of <code>collection</code> using <code>delimiter</code>.
     *
     * @param collection Objects to join their string representation.
     * @param delimiter  Delimiter to use while joining
     * @param <T>        What sort of collection element to expect
     * @return Joined collection. For example: (collection = ['a','b','c'],
     * delimiter =";") => 'a;b;c'.
     */
    public static <T> String join(final Collection<T> collection, final String delimiter) {
        String result = "";
        if (collection != null && !collection.isEmpty()) {
            final Collection<T> unmodifiableCollection = Collections.unmodifiableCollection(collection);
            final Iterator<T> iterator = unmodifiableCollection.iterator();
            if (iterator.hasNext()) {
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(iterator.next()));
                while (iterator.hasNext()) {
                    stringBuilder.append(delimiter).append(String.valueOf(iterator.next()));
                }
                result = stringBuilder.toString();
            }
        }
        return result;
    }

    /**
     * @param collection
     * @return joined string with default delimiter used.
     */
    public static <T> String join(final Collection<T> collection) {
        return join(collection, DEFAULT_COLLECTION_DELIMITER);
    }

    /**
     * @param map
     * @param delimiter
     * @return joined string
     */
    // @SuppressWarnings("unchecked")
    public static <K, V> String join(final Map<K, V> map, final String delimiter) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (map != null && !map.isEmpty()) {
            final Map<K, V> unmodifiableMap = Collections.unmodifiableMap(map);
            final Iterator<K> iterator = unmodifiableMap.keySet().iterator();
            if (iterator.hasNext()) {
                Object key = iterator.next();
                stringBuilder.append("MAP:[{" + key + ":" + unmodifiableMap.get(key) + "}");
                while (iterator.hasNext()) {
                    key = iterator.next();
                    stringBuilder.append(delimiter + "{" + key + ":" + unmodifiableMap.get(key) + "}");
                }
                stringBuilder.append("]");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param map
     * @return joined string with default delimiter used.
     */
    public static <K, V> String join(final Map<K, V> map) {
        return join(map, DEFAULT_COLLECTION_DELIMITER);
    }

    public static String join(final Object[] array) {
        return join(array, DEFAULT_COLLECTION_DELIMITER);
    }

    /**
     * Joins elements of <code>array</code> using <code>delimiter</code>.
     *
     * @param array     Object array to join their string representation.
     * @param delimiter Delimiter to use while joining
     * @return Joined collection. For example: (array = ['a','b','c'], delimiter
     * =";") => 'a;b;c'.
     */
    public static String join(final Object[] array, final String delimiter) {
        String result = "";
        if (array != null && array.length > 0) {
            // Grab value of first element
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(array[0]));
            // Iterate through remaining array of elements
            for (int i = 1; i < array.length; i++) {
                stringBuilder.append(delimiter).append(String.valueOf(array[i]));
            }
            result = stringBuilder.toString();
        }
        return result;
    }

    public static boolean isObjectNullOrTrimmedEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

    /**
     * @param value
     * @return trimmed value
     */
    public static String trimIfNotNull(String value) {
        if (value != null) {
            return value.trim();
        } else {
            return value;
        }
    }

    /**
     * @param fieldValue
     * @return value of object or empty string if object is null
     */
    public static String transformObjectValueToString(Object fieldValue) {
        return fieldValue != null ? fieldValue.toString() : "";
    }

    /**
     * Useful for cases where full length message is not desirable (e.g. to log
     * in non debug levels).<br/>
     * CAUTION: use it only for logging!
     *
     * @param message Message to shrink
     * @param maxSize If message length > this value, string will be reduced with
     *                ...(LN=$initialLength) addition.
     * @return Shrunk message.
     */
    public static String shrinkMessage(String message, int maxSize) {
        String shrunkMessage = message;
        if (message != null) {
            // NOTE: length reduced to avoid over blowing logs with too detailed
            // message.
            if (message.length() > maxSize) {
                shrunkMessage = String.format("%s...(LN=%s)", message.substring(0, maxSize), message.length());
            }
        }
        return shrunkMessage;
    }

}
