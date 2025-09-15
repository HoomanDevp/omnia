package com.omnia.log.splunk;

import java.util.List;

public class HttpEventCollectorMiddleware {
    private HttpSenderMiddleware httpSenderMiddleware = null;

    public HttpEventCollectorMiddleware() {
    }

    public void postEvents(List<HttpEventCollectorEventInfo> events, IHttpSender sender, IHttpSenderCallback callback) {
        if (this.httpSenderMiddleware == null) {
            sender.postEvents(events, callback);
        } else {
            this.httpSenderMiddleware.postEvents(events, sender, callback);
        }

    }

    public void add(HttpSenderMiddleware middleware) {
        if (this.httpSenderMiddleware != null) {
            middleware.next = this.httpSenderMiddleware;
        }
        this.httpSenderMiddleware = middleware;
    }

    public abstract static class HttpSenderMiddleware {
        private HttpSenderMiddleware next;

        public HttpSenderMiddleware() {
        }

        public abstract void postEvents(List<HttpEventCollectorEventInfo> var1, IHttpSender var2, IHttpSenderCallback var3);

        protected void callNext(List<HttpEventCollectorEventInfo> events, IHttpSender sender, IHttpSenderCallback callback) {
            if (this.next != null) {
                this.next.postEvents(events, sender, callback);
            } else {
                sender.postEvents(events, callback);
            }

        }
    }

    public interface IHttpSender {
        void postEvents(List<HttpEventCollectorEventInfo> var1, IHttpSenderCallback var2);
    }

    public interface IHttpSenderCallback {
        void completed(int var1, String var2);

        void failed(Exception var1);
    }
}
