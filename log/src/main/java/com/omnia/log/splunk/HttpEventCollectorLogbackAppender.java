package com.omnia.log.splunk;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpEventCollectorLogbackAppender extends AppenderBase<ILoggingEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpEventCollectorSender.TimeoutSettings timeoutSettings = new HttpEventCollectorSender.TimeoutSettings();

    private HttpEventCollectorSender sender = null;
    private boolean _includeLoggerName = true;
    private boolean _includeThreadName = true;
    private boolean _includeMDC = true;
    private boolean _includeException = true;
    private String _source;
    private String _sourcetype;
    private String _messageFormat;
    private String _host;
    private String _index;
    private String _url;
    private String _token;
    private String _channel;
    private String _disableCertificateValidation;
    private String _middleware;
    private String _eventBodySerializer;
    private String _eventHeaderSerializer;
    private String _errorCallback;
    private long _batchInterval = 0L;
    private long _batchCount = 0L;
    private long _batchSize = 0L;
    private String _sendMode;
    private long _retriesOnError = 0L;
    private Map<String, String> _metadata = new HashMap();

    public HttpEventCollectorLogbackAppender() {
    }

    public void start() {
        if (!this.started) {
            Map<String, String> metadata = getMetadata();

            this.sender = new HttpEventCollectorSender(this._url, this._token, this._channel, this._batchInterval, this._batchCount, this._batchSize, this._sendMode, metadata, this.timeoutSettings);
            if (this._middleware != null && !this._middleware.isEmpty()) {
                try {
                    this.sender.addMiddleware((HttpEventCollectorMiddleware.HttpSenderMiddleware) Class.forName(this._middleware).newInstance());
                } catch (Exception var5) {
                }
            }

            if (this._errorCallback != null && !this._errorCallback.isEmpty()) {
                HttpEventCollectorErrorHandler.registerClassName(this._errorCallback);
            }

            if (this._retriesOnError > 0L) {
                this.sender.addMiddleware(new HttpEventCollectorResendMiddleware(this._retriesOnError));
            }

            if (this._disableCertificateValidation != null && this._disableCertificateValidation.equalsIgnoreCase("true")) {
                this.sender.disableCertificateValidation();
            }

            super.start();

        }
    }

    private Map<String, String> getMetadata() {
        Map<String, String> metadata = new HashMap<>(this._metadata);
        if (this._host != null) {
            metadata.put("host", this._host);
        }

        if (this._index != null) {
            metadata.put("index", this._index);
        }

        if (this._source != null) {
            metadata.put("source", this._source);
        }

        if (this._sourcetype != null) {
            metadata.put("sourcetype", this._sourcetype);
        }

        if (this._messageFormat != null) {
            metadata.put("messageFormat", this._messageFormat);
        }
        return metadata;
    }

    public void flush() {
        if (this.started) {
            this.sender.flush();
        }

    }

    public void stop() {
        if (this.started) {
            this.sender.close();
            super.stop();
        }
    }

    protected void append(ILoggingEvent e) {
        this.sendEvent(e);
    }

    private void sendEvent(ILoggingEvent event) {
        event.prepareForDeferredProcessing();
        if (event.hasCallerData()) {
            event.getCallerData();
        }

        boolean isExceptionOccured = false;
        Map<Object, Object> exceptionDetailMap = new HashMap<>();

        try {
            IThrowableProxy throwableProxy = event.getThrowableProxy();
            if (Level.ERROR.isGreaterOrEqual(event.getLevel()) && throwableProxy != null) {
                exceptionDetailMap.put("detailMessage", throwableProxy.getMessage());
                exceptionDetailMap.put("exceptionClass", throwableProxy.getClassName());
                StackTraceElementProxy[] elements = throwableProxy.getStackTraceElementProxyArray();
                if (elements != null && elements.length > 0 && elements[0] != null) {
                    exceptionDetailMap.put("fileName", elements[0].getStackTraceElement().getFileName());
                    exceptionDetailMap.put("methodName", elements[0].getStackTraceElement().getMethodName());
                    exceptionDetailMap.put("lineNumber", String.valueOf(elements[0].getStackTraceElement().getLineNumber()));
                }

                isExceptionOccured = true;
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        if (this.started) {
            Map<String, String> mdcPropertyMap = new HashMap<>(event.getMDCPropertyMap());
            String userInfo = mdcPropertyMap.remove("user-info");
            String clientInfo = mdcPropertyMap.remove("client-info");
            Map<String, Object> mdc = new HashMap<>(mdcPropertyMap);
            try {
                if (StringUtils.hasText(userInfo)) {
                    mdc.put("user-info", objectMapper.readTree(userInfo));
                }
                if (StringUtils.hasText(clientInfo)) {
                    mdc.put("client-info", objectMapper.readTree(clientInfo));
                }

            } catch (Exception var8) {
                var8.printStackTrace();
            }
            Object message = event.getFormattedMessage();
            try {
                message = objectMapper.readTree(event.getFormattedMessage());
            } catch (Exception var9) {
//                var9.printStackTrace();
            }
            this.sender.send(event.getTimeStamp(),
                    event.getLevel().toString(),
                    message,
                    this._includeLoggerName ? event.getLoggerName() : null,
                    this._includeThreadName ? event.getThreadName() : null,
                    this._includeMDC ? mdc : null,
                    this._includeException && isExceptionOccured ? exceptionDetailMap : null);
        }

    }

    public void setUrl(String url) {
        this._url = url;
    }

    public String getUrl() {
        return this._url;
    }

    public void setToken(String token) {
        this._token = token;
    }

    public String getToken() {
        return this._token;
    }

    public void setChannel(String channel) {
        this._channel = channel;
    }

    public String getChannel() {
        return this._channel;
    }


    public boolean getIncludeLoggerName() {
        return this._includeLoggerName;
    }

    public void setIncludeLoggerName(boolean includeLoggerName) {
        this._includeLoggerName = includeLoggerName;
    }

    public boolean getIncludeThreadName() {
        return this._includeThreadName;
    }

    public void setIncludeThreadName(boolean includeThreadName) {
        this._includeThreadName = includeThreadName;
    }

    public boolean getIncludeMDC() {
        return this._includeMDC;
    }

    public void setIncludeMDC(boolean includeMDC) {
        this._includeMDC = includeMDC;
    }

    public boolean getIncludeException() {
        return this._includeException;
    }

    public void setIncludeException(boolean includeException) {
        this._includeException = includeException;
    }

    public void setSource(String source) {
        this._source = source;
    }

    public String getSource() {
        return this._source;
    }

    public void setSourcetype(String sourcetype) {
        this._sourcetype = sourcetype;
    }

    public String getSourcetype() {
        return this._sourcetype;
    }

    public void setMessageFormat(String messageFormat) {
        this._messageFormat = messageFormat;
    }

    public String getMessageFormat() {
        return this._messageFormat;
    }

    public void setHost(String host) {
        this._host = host;
    }

    public String getHost() {
        return this._host;
    }

    public void setIndex(String index) {
        this._index = index;
    }

    public String getIndex() {
        return this._index;
    }

    public void addMetadata(String tag, String value) {
        this._metadata.put(tag, value);
    }

    public String getEventBodySerializer() {
        return this._eventBodySerializer;
    }

    public String getEventHeaderSerializer() {
        return this._eventHeaderSerializer;
    }

    public String getErrorHandler(String errorHandlerClass) {
        return this._errorCallback;
    }

    public void setDisableCertificateValidation(String disableCertificateValidation) {
        this._disableCertificateValidation = disableCertificateValidation;
    }

    public void setbatch_size_count(String value) {
        this._batchCount = parseLong(value, 10);
    }

    public void setbatch_size_bytes(String value) {
        this._batchSize = parseLong(value, 10240);
    }

    public void setbatch_interval(String value) {
        this._batchInterval = parseLong(value, 10000);
    }

    public void setretries_on_error(String value) {
        this._retriesOnError = parseLong(value, 0);
    }

    public void setsend_mode(String value) {
        this._sendMode = value;
    }

    public void setmiddleware(String value) {
        this._middleware = value;
    }

    public String getDisableCertificateValidation() {
        return this._disableCertificateValidation;
    }

    public void setEventBodySerializer(String eventBodySerializer) {
        this._eventBodySerializer = eventBodySerializer;
    }

    public void setEventHeaderSerializer(String eventHeaderSerializer) {
        this._eventHeaderSerializer = eventHeaderSerializer;
    }

    public void setErrorCallback(String errorHandlerClass) {
        this._errorCallback = errorHandlerClass;
    }

    public String getErrorCallback() {
        return this._errorCallback;
    }

    public void setConnectTimeout(long milliseconds) {
        this.timeoutSettings.connectTimeout = milliseconds;
    }

    public long getConnectTimeout(long milliseconds) {
        return this.timeoutSettings.connectTimeout = milliseconds;
    }

    public void setCallTimeout(long milliseconds) {
        this.timeoutSettings.callTimeout = milliseconds;
    }

    public long getCallTimeout(long milliseconds) {
        return this.timeoutSettings.callTimeout = milliseconds;
    }

    public void setReadTimeout(long milliseconds) {
        this.timeoutSettings.readTimeout = milliseconds;
    }

    public long getReadTimeout(long milliseconds) {
        return this.timeoutSettings.readTimeout = milliseconds;
    }

    public void setWriteTimeout(long milliseconds) {
        this.timeoutSettings.writeTimeout = milliseconds;
    }

    public long getWriteTimeout(long milliseconds) {
        return this.timeoutSettings.writeTimeout = milliseconds;
    }

    public void setTerminationTimeout(long milliseconds) {
        this.timeoutSettings.terminationTimeout = milliseconds;
    }

    public long getTerminationTimeout(long milliseconds) {
        return this.timeoutSettings.terminationTimeout = milliseconds;
    }

    private static long parseLong(String string, int defaultValue) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException var3) {
            return (long) defaultValue;
        }
    }
}
