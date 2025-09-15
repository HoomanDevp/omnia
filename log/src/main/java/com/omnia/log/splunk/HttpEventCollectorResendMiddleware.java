package com.omnia.log.splunk;

import java.util.List;

public class HttpEventCollectorResendMiddleware extends HttpEventCollectorMiddleware.HttpSenderMiddleware {
    private long retriesOnError = 0L;

    public HttpEventCollectorResendMiddleware(long retriesOnError) {
        this.retriesOnError = retriesOnError;
    }
    public void postEvents(List<HttpEventCollectorEventInfo> events, HttpEventCollectorMiddleware.IHttpSender sender, HttpEventCollectorMiddleware.IHttpSenderCallback callback) {
        this.callNext(events, sender, new Callback(events, sender, callback));
    }

    private class Callback implements HttpEventCollectorMiddleware.IHttpSenderCallback {
        private long retries = 0L;
        private final List<HttpEventCollectorEventInfo> events;
        private HttpEventCollectorMiddleware.IHttpSenderCallback prevCallback;
        private HttpEventCollectorMiddleware.IHttpSender sender;
        private final long RetryDelayCeiling = 60000L;
        private long retryDelay = 1000L;

        public Callback(List<HttpEventCollectorEventInfo> events, HttpEventCollectorMiddleware.IHttpSender sender, HttpEventCollectorMiddleware.IHttpSenderCallback prevCallback) {
            this.events = events;
            this.prevCallback = prevCallback;
            this.sender = sender;
        }

        public void completed(int statusCode, String reply) {
            this.prevCallback.completed(statusCode, reply);
        }

        public void failed(Exception ex) {
            if (this.retries < HttpEventCollectorResendMiddleware.this.retriesOnError) {
                ++this.retries;

                try {
                    Thread.sleep(this.retryDelay);
                    HttpEventCollectorResendMiddleware.this.callNext(this.events, this.sender, this);
                } catch (InterruptedException ie) {
                    this.prevCallback.failed(ie);
                }

                this.retryDelay = Math.min(60000L, this.retryDelay * 2L);
            } else {
                this.prevCallback.failed(ex);
            }

        }
    }
}
