package com.meriosol.util;

/**
 * Utilities for exceptions (mostly for logging).
 *
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class ExceptionUtil {
    private static final int MAX_SHORT_EXCEPTION_LENGTH = 200;
    private static final int MAX_CAUSE_LEVEL = 15;

    private ExceptionUtil() {
    }

    /**
     * Creates full stack trace info for all master and cause exceptions.
     *
     * @param throwable Master exception
     * @return Exception stack trace info
     */
    public static String buildStackTrace(Throwable throwable) {
        final StringBuffer stackTraceBuffer = new StringBuffer("");
        if (throwable != null) {
            final String excMessage = throwable.getMessage();
            stackTraceBuffer.append("==> Exception message: ");
            stackTraceBuffer.append(excMessage == null ? "" : excMessage);
            stackTraceBuffer.append("\n");
            final int causeLevel = 1;
            stackTraceBuffer.append(buildCauseStackTrace(throwable.getCause(), causeLevel, MAX_CAUSE_LEVEL));
        }
        return stackTraceBuffer.toString();
    }


    /**
     * @param e
     * @return Short Exception Info
     */
    public static String createShortExceptionInfo(Throwable e) {
        return createShortExceptionInfo(e, MAX_SHORT_EXCEPTION_LENGTH);
    }

    /**
     * @param e
     * @param maxMessageLength
     * @return Short Exception Info
     */
    public static String createShortExceptionInfo(Throwable e, int maxMessageLength) {
        String shortExceptionInfo = "";
        if (e != null) {
            String stackTrace = ExceptionUtil.buildStackTrace(e);
            if (!Util.isObjectNullOrTrimmedEmpty(stackTrace)) {
                shortExceptionInfo = stackTrace.trim();
                if (shortExceptionInfo.length() > maxMessageLength) {
                    shortExceptionInfo = shortExceptionInfo.substring(0, maxMessageLength) + "..";
                }
            }
        }
        return shortExceptionInfo;
    }

    private static StringBuffer buildCauseStackTrace(Throwable cause, int causeLevel,
                                                     int maxCauseLevel) {
        final StringBuffer stackTraceBuffer = new StringBuffer("");
        if (causeLevel <= maxCauseLevel) {
            if (cause != null) {
                stackTraceBuffer.append(" ==> Cause exc message: " + cause.getMessage());
                stackTraceBuffer.append("\n");
                stackTraceBuffer.append(" ==> Cause stackTrace for level = " + causeLevel + ":"
                        + "\n");
                stackTraceBuffer.append(buildStackTraceElements(cause.getStackTrace()));
                // recursive add cause traces
                stackTraceBuffer.append(buildCauseStackTrace(cause.getCause(), causeLevel + 1,
                        maxCauseLevel));
            } else {
                // stackTraceBuffer.append(" No more causes." + "\n");
                stackTraceBuffer.append(" ==> END." + "\n");
            }
        }
        return stackTraceBuffer;
    }

    private static StringBuffer buildStackTraceElements(StackTraceElement[] stackTraceElements) {
        final StringBuffer stackTraceBuffer = new StringBuffer("");
        if ((stackTraceElements != null) && (stackTraceElements.length > 0)) {
            for (int i = 0; i < stackTraceElements.length; i++) {
                final StackTraceElement stackTraceElement = stackTraceElements[i];
                // String formattedTrace = "{EXC_TRACE[" + (i + 1) + "]: CLASS=["
                // + stackTraceElement.getClassName() + "]," + "METHOD=["
                // + stackTraceElement.getMethodName() + "]," + "LINE=["
                // + stackTraceElement.getLineNumber() + "]" + "}";
                final String formattedTrace = "   ..[" + (i + 1) + "] at "
                        + stackTraceElement.getClassName() + " ("
                        + stackTraceElement.getMethodName() + ":"
                        + stackTraceElement.getLineNumber() + ")";
                stackTraceBuffer.append(formattedTrace + "\n");
            }
        }
        return stackTraceBuffer;
    }
}
