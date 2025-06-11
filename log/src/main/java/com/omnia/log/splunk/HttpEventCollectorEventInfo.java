package com.omnia.log.splunk;

import java.util.Map;

public class HttpEventCollectorEventInfo {
    private final double time;
    private final String severity;
    private final Object message;
    private final String logger_name;
    private final String thread_name;
    private final Map<String, Object> properties;
    private final Map<Object, Object> exception_message;

    public HttpEventCollectorEventInfo(long timeMsSinceEpoch, String severity, Object message, String logger_name, String thread_name, Map<String, Object> properties,
                                       Map<Object, Object> exception_message) {
        this.time = (double) timeMsSinceEpoch / (double) 1000.0F;
        this.severity = severity;
        this.message = message;
        this.logger_name = logger_name;
        this.thread_name = thread_name;
        this.properties = properties;
        this.exception_message = exception_message;
    }

    public double getTime() {
        return this.time;
    }

    public final String getSeverity() {
        return this.severity;
    }

    public final Object getMessage() {
        return this.message;
    }

    public final String getLoggerName() {
        return this.logger_name;
    }

    public final String getThreadName() {
        return this.thread_name;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public final Map<Object,Object> getExceptionMessage() {
        return this.exception_message;
    }

}
