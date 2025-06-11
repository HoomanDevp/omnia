package com.omnia.log.splunk;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class HttpEventCollectorErrorHandler {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static ErrorCallback errorCallback;

    public HttpEventCollectorErrorHandler() {
    }

    public static void registerClassName(String errorCallbackClass) {
        if (errorCallbackClass != null && !errorCallbackClass.trim().isEmpty()) {
            try {
                ErrorCallback callback = (ErrorCallback) Class.forName(errorCallbackClass).newInstance();
                onError(callback);
            } catch (Exception e) {
                System.err.println("Warning: cannot create ErrorCallback instance: " + e);
            }

        } else {
            onError((ErrorCallback) null);
        }
    }

    public static void onError(ErrorCallback callback) {
        if (callback == null) {
            logInfo("Reset ErrorCallback to null (no error handling).");
        } else {
            logInfo("Register ErrorCallback implementation: " + callback);
            if (errorCallback != null && !errorCallback.equals(callback)) {
                logWarn("ErrorCallback instance of '" + errorCallback.getClass().getName() + "' will be replaced by handler instance of '" + callback.getClass().getName() + "'");
            }
        }

        errorCallback = callback;
    }

    public static void error(List<HttpEventCollectorEventInfo> data, Exception ex) {
        if (errorCallback != null) {
            errorCallback.error(data, ex);
        }

    }

    private static void logInfo(String message) {
        System.out.println("Info: " + message);
    }

    private static void logWarn(String message) {
        System.out.println("Warning: " + message);
    }

    public static class ServerErrorException extends Exception {
        private String reply;
        private long errorCode = -1L;
        private String errorText;

        public ServerErrorException(String serverReply) {
            this.reply = serverReply;

            try {
                JsonNode jsonNode = objectMapper.readTree(serverReply);
                this.errorCode = jsonNode.get("code").asLong();
                this.errorText = jsonNode.get("text").asText();
            } catch (Exception e) {
                this.errorText = e.getMessage();
            }

        }

        public String getReply() {
            return this.reply;
        }

        public long getErrorCode() {
            return this.errorCode;
        }

        public String getErrorText() {
            return this.errorText;
        }

        public String getMessage() {
            return this.getErrorText();
        }

        public String toString() {
            return this.getReply();
        }
    }

    public interface ErrorCallback {
        void error(List<HttpEventCollectorEventInfo> var1, Exception var2);
    }
}
