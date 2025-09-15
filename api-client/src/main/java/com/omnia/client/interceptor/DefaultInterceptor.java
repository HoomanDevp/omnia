package com.omnia.client.interceptor;

import com.omnia.client.exception.MissingHeaderException;
import com.omnia.core.header.constant.HeaderKey;
import com.omnia.core.header.model.HeaderSpec;
import com.omnia.core.security.UserDataHolder;
import com.omnia.log.AppLogger;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class DefaultInterceptor implements Interceptor {
    private final AppLogger appLogger = new AppLogger(DefaultInterceptor.class);
    private final boolean propagateHeaders;

    /**
     * Intercepts outgoing HTTP requests to ensure required headers (JOB_ID, MSG_ID) are present.
     * Populates headers from HeaderSpec if available, otherwise falls back to MDC.
     * Optionally propagates extra headers from HeaderSpec if enabled.
     * Logs request processing steps and throws MissingHeaderException if required headers are missing.
     *
     * @param chain the request chain to intercept
     * @return the HTTP response after processing the updated request
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Headers headers = originalRequest.headers();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        appLogger.infoF("Intercepting request. url={} method={}", originalRequest.url(), originalRequest.method());

        HeaderSpec headerSpec = UserDataHolder.get();

        if (headerSpec != null) {
            appLogger.infoF("HeaderSpec found. url={} method={} jobId={} msgId={}", originalRequest.url(), originalRequest.method(), headerSpec.getJobId(), headerSpec.getMsgId());

            if (!StringUtils.hasText(headers.get(HeaderKey.JOB_ID.getKey()))) {
                requestBuilder.header(HeaderKey.JOB_ID.getKey(), headerSpec.getJobId());
                appLogger.infoF("Added JOB_ID from HeaderSpec. url={} method={} jobId={} msgId={}", originalRequest.url(), originalRequest.method(), headerSpec.getJobId(), headerSpec.getMsgId());
            }

            if (!StringUtils.hasText(headers.get(HeaderKey.MSG_ID.getKey()))) {
                requestBuilder.header(HeaderKey.MSG_ID.getKey(), headerSpec.getMsgId());
                appLogger.infoF("Added MSG_ID from HeaderSpec.url={} method={} jobId={} msgId={}", originalRequest.url(), originalRequest.method(), headerSpec.getJobId(), headerSpec.getMsgId());
            }

            if (propagateHeaders) {
                appLogger.infoF("Propagated extra header. url={} method={} jobId={} msgId={}", originalRequest.url(), originalRequest.method(), headerSpec.getJobId(), headerSpec.getMsgId());
                headerSpec.getHeaders().forEach((key, value) -> {
                    if (!StringUtils.hasText(headers.get(key))) {
                        requestBuilder.header(key, value);
                    }
                });
            }

        } else {
            appLogger.warnF("No HeaderSpec found. Falling back to MDC. url={} method={}", originalRequest.url(), originalRequest.method());

            if (!StringUtils.hasText(headers.get(HeaderKey.JOB_ID.getKey()))) {
                String jobId = MDC.get(HeaderKey.JOB_ID.getKey());
                if (StringUtils.hasText(jobId)) {
                    requestBuilder.header(HeaderKey.JOB_ID.getKey(), jobId);
                    appLogger.infoF("Added JOB_ID from MDC. url={} method={} job-id={}", originalRequest.url(), originalRequest.method(), jobId);
                } else {
                    appLogger.errorF("Missing JOB_ID in both HeaderSpec and MDC. url={} method={}", originalRequest.url(), originalRequest.method());
                    throw new MissingHeaderException(HeaderKey.JOB_ID.getKey());
                }
            }

            if (!StringUtils.hasText(headers.get(HeaderKey.MSG_ID.getKey()))) {
                String msgId = MDC.get(HeaderKey.MSG_ID.getKey());
                if (StringUtils.hasText(msgId)) {
                    requestBuilder.header(HeaderKey.MSG_ID.getKey(), msgId);
                    appLogger.infoF("Added MSG_ID from MDC. url={} method={} msg-id={}", originalRequest.url(), originalRequest.method(), msgId);
                } else {
                    appLogger.errorF("Missing MSG_ID in both HeaderSpec and MDC. url={} method={}", originalRequest.url(), originalRequest.method());
                    throw new MissingHeaderException(HeaderKey.MSG_ID.getKey());
                }
            }
        }

        requestBuilder.header(HttpHeaders.CONNECTION, "close");
        appLogger.infoF("Proceeding with updated request. url={} method={}", originalRequest.url(), originalRequest.method());
        return chain.proceed(requestBuilder.build());
    }
}
