package com.omnia.client.interceptor;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.log.AppLogger;
import com.omnia.log.constant.RequestLogAttribute;
import com.omnia.log.constant.ResponseLogAttribute;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class LoggingInterceptor implements Interceptor {
    private final AppLogger appLogger = new AppLogger(LoggingInterceptor.class);

    private final MeterRegistry meterRegistry;

    @Override
    public Response intercept(Chain chain) throws IOException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Request request = chain.request();
        Buffer buffer = new Buffer();
        if (request.body() != null) {
            request.body().writeTo(buffer);
        }
        byte[] requestBody = buffer.readByteArray();
        buffer.close();

        Request logRequest = request.newBuilder()
                .method(request.method(), requestBody.length != 0 && request.body() != null ? RequestBody.create(requestBody, request.body().contentType()) : null)
                .build();

        Request orginalRequest = request.newBuilder()
                .method(request.method(), requestBody.length != 0 && request.body() != null ? RequestBody.create(requestBody, request.body().contentType()) : null)
                .build();

        try {
            Response response = chain.proceed(orginalRequest);
            stopWatch.stop();
            appLogger.info(GatewayLogSpec.of(logRequest, response, stopWatch.getTotalTimeMillis()));
            recordMetrics(request, response);
            return response;
        } catch (Exception e) {
            stopWatch.stop();
            appLogger.info(GatewayLogSpec.of(logRequest, e, stopWatch.getTotalTimeMillis()));
            recordMetrics(request);
            throw e;
        }

    }

    private void recordMetrics(Request request, Response response) {
        List<Tag> tags = List.of(
                Tag.of(RequestLogAttribute.IP.getKey(), request.url().host()),
                Tag.of(RequestLogAttribute.METHOD.getKey(), request.method()),
                Tag.of(RequestLogAttribute.URI.getKey(), request.url().uri().getPath()),
                Tag.of(ResponseLogAttribute.STATUS.getKey(), String.valueOf(response.code()))
        );

        String counterName = response.isSuccessful() ? OmniaConstants.GATEWAY_SUCCESS_COUNTER : OmniaConstants.GATEWAY_ERROR_COUNTER;
        meterRegistry.counter(counterName, tags).increment();
        meterRegistry.counter(OmniaConstants.GATEWAY_COUNTER, tags).increment();
    }

    private void recordMetrics(Request request) {
        List<Tag> tags = List.of(
                Tag.of(RequestLogAttribute.IP.getKey(), request.url().host()),
                Tag.of(RequestLogAttribute.METHOD.getKey(), request.method()),
                Tag.of(RequestLogAttribute.URI.getKey(), request.url().uri().getPath()),
                Tag.of(ResponseLogAttribute.STATUS.getKey(), String.valueOf(0))
        );

        String counterName = OmniaConstants.GATEWAY_ERROR_COUNTER;
        meterRegistry.counter(counterName, tags).increment();
        meterRegistry.counter(OmniaConstants.GATEWAY_COUNTER, tags).increment();
    }
}