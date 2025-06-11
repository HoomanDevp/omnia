package ir.stts.bajet.log.splunk;


import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HttpEventCollectorSender extends TimerTask implements HttpEventCollectorMiddleware.IHttpSender {
    private static final String ChannelQueryParam = "channel";
    private static final String AuthorizationHeaderTag = "Authorization";
    private static final String AuthorizationHeaderScheme = "Splunk %s";
    private static final String HttpEventCollectorUriPath = "/services/collector/event/1.0";
    private static final String HttpRawCollectorUriPath = "/services/collector/raw";
    private static final String JsonHttpContentType = "application/json; profile=\"urn:splunk:event:1.0\"; charset=utf-8";
    private static final String PlainTextHttpContentType = "plain/text; charset=utf-8";
    private static final String SendModeSequential = "sequential";
    private static final String SendModeSParallel = "parallel";
    private TimeoutSettings timeoutSettings = new TimeoutSettings();
    private final HecJsonSerializer serializer;
    public static final int DefaultBatchInterval = 10000;
    public static final int DefaultBatchSize = 10240;
    public static final int DefaultBatchCount = 10;
    private HttpUrl url;
    private String token;
    private String channel;
    private long maxEventsBatchCount;
    private long maxEventsBatchSize;
    private Timer timer;
    private List<HttpEventCollectorEventInfo> eventsBatch = new LinkedList();
    private long eventsBatchSize = 0L;
    private static final OkHttpClient httpSharedClient = new OkHttpClient();
    private OkHttpClient httpClient = null;
    private boolean disableCertificateValidation = false;
    private SendMode sendMode;
    private HttpEventCollectorMiddleware middleware;

    public HttpEventCollectorSender(String Url, String token, String channel, long delay, long maxEventsBatchCount, long maxEventsBatchSize, String sendModeStr, Map<String, String> metadata, TimeoutSettings timeoutSettings) {
        this.sendMode = HttpEventCollectorSender.SendMode.Sequential;
        this.middleware = new HttpEventCollectorMiddleware();
        this.token = token;
        this.channel = channel;
        if (timeoutSettings != null) {
            this.timeoutSettings = timeoutSettings;
        }

        this.url = HttpUrl.parse(Url + "/services/collector/event/1.0");

        if (maxEventsBatchCount == 0L && maxEventsBatchSize > 0L) {
            maxEventsBatchCount = Long.MAX_VALUE;
        } else if (maxEventsBatchSize == 0L && maxEventsBatchCount > 0L) {
            maxEventsBatchSize = Long.MAX_VALUE;
        }

        this.maxEventsBatchCount = maxEventsBatchCount;
        this.maxEventsBatchSize = maxEventsBatchSize;
        this.serializer = new HecJsonSerializer(metadata);
        String format = (String) metadata.get("messageFormat");
        if (sendModeStr != null) {
            if (sendModeStr.equals("sequential")) {
                this.sendMode = HttpEventCollectorSender.SendMode.Sequential;
            } else {
                if (!sendModeStr.equals("parallel")) {
                    throw new IllegalArgumentException("Unknown send mode: " + sendModeStr);
                }

                this.sendMode = HttpEventCollectorSender.SendMode.Parallel;
            }
        }

        if (delay > 0L) {
            this.timer = new Timer(true);
            this.timer.scheduleAtFixedRate(this, delay, delay);
        }

    }

    public void addMiddleware(HttpEventCollectorMiddleware.HttpSenderMiddleware middleware) {
        this.middleware.add(middleware);
    }

    public synchronized void send(long timeMsSinceEpoch, String severity, Object message, String logger_name, String thread_name, Map<String, Object> properties, Map<Object, Object> exception_message) {
        HttpEventCollectorEventInfo eventInfo = new HttpEventCollectorEventInfo(timeMsSinceEpoch, severity, message, logger_name, thread_name, properties, exception_message);
        this.eventsBatch.add(eventInfo);
        if ((long) this.eventsBatch.size() >= this.maxEventsBatchCount) {
            this.flush();
        }

    }

    public synchronized void flush() {
        this.flush(false);
    }

    private synchronized void flushEvents() {
        if (this.eventsBatch.size() > 0) {
            this.postEventsAsync(this.eventsBatch);
        }

        this.eventsBatch = new LinkedList();
        this.eventsBatchSize = 0L;
    }

    public synchronized void flush(boolean close) {
        this.flushEvents();
        if (close) {
            this.stopHttpClient();
        } else {
            this.flushHttpClient();
        }

    }

    void close() {
        if (this.timer != null) {
            this.timer.cancel();
        }

        this.flush(true);
        super.cancel();
    }

    public void run() {
        this.flushEvents();
    }

    public void disableCertificateValidation() {
        this.disableCertificateValidation = true;
    }


    private void flushHttpClient() {
        this.flushHttpClient(this.timeoutSettings.terminationTimeout);
    }

    private void flushHttpClient(long timeout) {
        if (this.httpClient != null && timeout > 0L) {
            Dispatcher dispatcher = this.httpClient.dispatcher();
            long start = System.currentTimeMillis();

            while (dispatcher.queuedCallsCount() > 0 && dispatcher.runningCallsCount() > 0 && start + timeout > System.currentTimeMillis()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(30L);
                } catch (InterruptedException var7) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

    }

    private void stopHttpClient() {
        if (this.httpClient != null) {
            Dispatcher dispatcher = this.httpClient.dispatcher();
            this.httpClient = null;
            if (this.timeoutSettings.terminationTimeout > 0L) {
                long start = System.currentTimeMillis();

                while (dispatcher.queuedCallsCount() > 0 && start + this.timeoutSettings.terminationTimeout > System.currentTimeMillis()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10L);
                    } catch (InterruptedException var8) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                dispatcher.executorService().shutdown();
                long awaitTerminationTimeout = this.timeoutSettings.terminationTimeout - (System.currentTimeMillis() - start);
                if (awaitTerminationTimeout > 0L) {
                    try {
                        dispatcher.executorService().awaitTermination(awaitTerminationTimeout, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException var7) {
                        Thread.currentThread().interrupt();
                    }
                }
            } else {
                dispatcher.executorService().shutdown();
            }
        }

    }

    private void startHttpClient() {
        if (this.httpClient == null) {
            OkHttpClient.Builder builder = httpSharedClient.newBuilder();
            builder.connectTimeout(this.timeoutSettings.connectTimeout, TimeUnit.MILLISECONDS).callTimeout(this.timeoutSettings.callTimeout, TimeUnit.MILLISECONDS).readTimeout(this.timeoutSettings.readTimeout, TimeUnit.MILLISECONDS).writeTimeout(this.timeoutSettings.writeTimeout, TimeUnit.MILLISECONDS);
            Dispatcher dispatcher = new Dispatcher();
            if (this.sendMode == HttpEventCollectorSender.SendMode.Sequential) {
                dispatcher.setMaxRequests(1);
            }

            builder.dispatcher(dispatcher);
            if (this.disableCertificateValidation) {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }};

                try {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init((KeyManager[]) null, trustAllCerts, new SecureRandom());
                    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                    builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                } catch (Exception var6) {
                }

                builder.hostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            this.httpClient = builder.build();
        }
    }

    private void postEventsAsync(final List<HttpEventCollectorEventInfo> events) {
        this.middleware.postEvents(events, this, new HttpEventCollectorMiddleware.IHttpSenderCallback() {
            public void completed(int statusCode, String reply) {
                if (statusCode != 200) {
                    HttpEventCollectorErrorHandler.error(events, new HttpEventCollectorErrorHandler.ServerErrorException(reply));
                }

            }

            public void failed(Exception exception) {
                HttpEventCollectorErrorHandler.error(events, exception);
            }
        });
    }

    public void postEvents(List<HttpEventCollectorEventInfo> events, final HttpEventCollectorMiddleware.IHttpSenderCallback callback) {
        this.startHttpClient();
        Request.Builder requestBldr = (new Request.Builder()).url(this.url).addHeader("Authorization", String.format("Splunk %s", this.token));

        StringBuilder eventsBatchString = new StringBuilder();

        for (HttpEventCollectorEventInfo eventInfo : events) {
            eventsBatchString.append(this.serializer.serialize(eventInfo));
        }
        RequestBody requestBody = RequestBody.create(eventsBatchString.toString().getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json; profile=\"urn:splunk:event:1.0\"; charset=utf-8"));
        requestBldr.post(requestBody);


        this.httpClient.newCall(requestBldr.build()).enqueue(new Callback() {
            public void onResponse(Call call, Response response) {
                String reply = "";
                int httpStatusCode = response.code();

                try (ResponseBody body = response.body()) {
                    if (httpStatusCode != 200 && body != null) {
                        try {
                            reply = body.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            reply = e.getMessage();
                        }
                    }
                }

                callback.completed(httpStatusCode, reply);
            }

            public void onFailure(Call call, IOException ex) {
                ex.printStackTrace();
                callback.failed(ex);
            }
        });
    }

    public static enum SendMode {
        Sequential,
        Parallel;

        private SendMode() {
        }
    }

    public static class TimeoutSettings {
        public static final long DEFAULT_CONNECT_TIMEOUT = 3000L;
        public static final long DEFAULT_WRITE_TIMEOUT = 10000L;
        public static final long DEFAULT_CALL_TIMEOUT = 0L;
        public static final long DEFAULT_READ_TIMEOUT = 10000L;
        public static final long DEFAULT_TERMINATION_TIMEOUT = 0L;
        public long connectTimeout = 3000L;
        public long callTimeout = 0L;
        public long readTimeout = 10000L;
        public long writeTimeout = 10000L;
        public long terminationTimeout = 0L;

        public TimeoutSettings() {
        }

        public TimeoutSettings(long connectTimeout, long callTimeout, long readTimeout, long writeTimeout, long terminationTimeout) {
            this.connectTimeout = connectTimeout;
            this.callTimeout = callTimeout;
            this.readTimeout = readTimeout;
            this.writeTimeout = writeTimeout;
            this.terminationTimeout = terminationTimeout;
        }
    }
}
