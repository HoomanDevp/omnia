package com.omnia.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.dto.OmniaErrorResponseDto;
import com.omnia.core.header.config.HeaderActionProperties;
import com.omnia.core.header.config.HeaderValidation;
import com.omnia.core.header.constant.HeaderKey;
import com.omnia.core.header.constant.HeaderValidationStatus;
import com.omnia.core.header.model.ClientInfo;
import com.omnia.core.header.model.HeaderSpec;
import com.omnia.core.header.model.UserInfo;
import com.omnia.core.model.RequestLogSpec;
import com.omnia.core.resilience.exception.OmniaException;
import com.omnia.core.resilience.handler.GlobalExceptionHandler;
import com.omnia.core.uniqueref.JobIdGenerator;
import com.omnia.core.uniqueref.MessageIdGenerator;
import com.omnia.log.AppLogger;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.Priority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Priority(1)
@RequiredArgsConstructor
public class HeaderFilter extends CustomOncePerRequestFilter {
    private final AppLogger appLogger = new AppLogger(HeaderFilter.class);
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;
    private final JobIdGenerator jobIdGenerator;
    private final MessageIdGenerator msgIdGenerator;
    private final HeaderActionProperties properties;
    private final HeaderValidation headerValidation;
    private final GlobalExceptionHandler exceptionHandler;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("HEADER_FILTER");
        appLogger.info("Header filter logger started.");
        initMDC();

        String jobId = StringUtils.hasText(request.getHeader(HeaderKey.JOB_ID.getKey())) ? request.getHeader(HeaderKey.JOB_ID.getKey()) : jobIdGenerator.generateId();
        String msgId = StringUtils.hasText(request.getHeader(HeaderKey.MSG_ID.getKey())) ? request.getHeader(HeaderKey.MSG_ID.getKey()) : msgIdGenerator.generateId();
        MDC.put(HeaderKey.JOB_ID.getKey(), jobId);
        MDC.put(HeaderKey.MSG_ID.getKey(), msgId);
        HeaderSpec headerSpec = new HeaderSpec()
                .setLang(request.getHeader(HeaderKey.ACCEPT_LANGUAGE.getKey()))
                .setJobId(jobId)
                .setMsgId(msgId);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        responseWrapper.addHeader(HeaderKey.JOB_ID.getKey(), jobId);
        responseWrapper.addHeader(HeaderKey.MSG_ID.getKey(), msgId);
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);


        String userInfoHeader = requestWrapper.getHeader(HeaderKey.USER_INFO.getKey());
        if (StringUtils.hasText(userInfoHeader)) {
            headerSpec.setUserInfo(objectMapper.readValue(userInfoHeader, UserInfo.class));
            MDC.put(HeaderKey.USER_INFO.getKey(), userInfoHeader);
        }

        String clientInfoHeader = requestWrapper.getHeader(HeaderKey.CLIENT_INFO.getKey());
        if (StringUtils.hasText(clientInfoHeader)) {
            headerSpec.setClientInfo(objectMapper.readValue(clientInfoHeader, ClientInfo.class));
            MDC.put(HeaderKey.CLIENT_INFO.getKey(), clientInfoHeader);
        }


        HeaderValidationStatus validationStatus = headerValidation.validate(headerSpec, properties);
        if (validationStatus == HeaderValidationStatus.IGNORED) {

            try {
                filterChain.doFilter(requestWrapper, responseWrapper);
            } finally {
                stopWatch.stop();
                appLogger.info("Header filter finished without validation ", String.format("%dms", stopWatch.getTotalTimeMillis()));
                this.logRequest(requestWrapper, responseWrapper, stopWatch);
                destroyMDC();
            }

            return;
        }

        if (validationStatus == HeaderValidationStatus.INVALID) {

            createResponseError(responseWrapper, new OmniaErrorResponseDto("", "BAD DATA", false));

            stopWatch.stop();
            appLogger.info("Header filter finished with error in ", String.format("%dms", stopWatch.getTotalTimeMillis()));
            this.logRequest(requestWrapper, responseWrapper, stopWatch);
            destroyMDC();
            return;
        }

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (OmniaException e) {
            ResponseEntity<OmniaErrorResponseDto> responseEntity = exceptionHandler.handleBajetException(e);
            createResponseError(responseWrapper, responseEntity.getBody());
        } catch (Exception e) {
            ResponseEntity<Object> responseEntity = exceptionHandler.handleException(e);
            createResponseError(responseWrapper, (OmniaErrorResponseDto) responseEntity.getBody());
        } finally {
            stopWatch.stop();
            appLogger.info("Header filter finished completely in ", String.format("%dms", stopWatch.getTotalTimeMillis()));
            this.logRequest(requestWrapper, responseWrapper, stopWatch);
            destroyMDC();
        }
    }

    @Override
    protected boolean shouldNotFilter(@Nullable HttpServletRequest request) throws ServletException {

        if (request == null) return false;

        return super.shouldNotFilter(request);
    }

    @Override
    protected ObjectMapper getObjectMapper() {

        return this.objectMapper;
    }

    private void logRequest(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper, StopWatch stopWatch) {
        appLogger.info(RequestLogSpec.of(requestWrapper, responseWrapper, stopWatch.getTotalTimeMillis()));

        List<Tag> tags = extractTag(requestWrapper, responseWrapper);
        meterRegistry.counter(OmniaConstants.HTTP_COUNTER, tags).increment();
        if (HttpStatus.valueOf(responseWrapper.getStatus()).is2xxSuccessful())
            meterRegistry.counter(OmniaConstants.HTTP_SUCCESS_COUNTER, tags).increment();
        else meterRegistry.counter(OmniaConstants.HTTP_ERROR_COUNTER, tags).increment();
    }

    public static List<Tag> extractTag(HttpServletRequest request, HttpServletResponse response) {

        List<Tag> tags = new ArrayList<>();
        tags.add(new ImmutableTag("method", request.getMethod()));
        tags.add(new ImmutableTag("uri", request.getRequestURI()));
        tags.add(new ImmutableTag("status", String.valueOf(response.getStatus())));

        return tags;
    }
}