package com.omnia.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.dto.BajetErrorResponseDto;
import com.omnia.core.header.constant.HeaderKey;
import com.omnia.log.AppLogger;
import jakarta.annotation.Nullable;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class CustomOncePerRequestFilter extends OncePerRequestFilter {
    private final AppLogger appLogger = new AppLogger(CustomOncePerRequestFilter.class);

    protected abstract ObjectMapper getObjectMapper();

    protected void initMDC() {

        MDC.put(HeaderKey.JOB_ID.getKey(), "0");
        MDC.put(HeaderKey.MSG_ID.getKey(), "0");
        MDC.put(HeaderKey.USER_INFO.getKey(), "{}");
        MDC.put(HeaderKey.CLIENT_INFO.getKey(), "{}");
    }

    protected void destroyMDC() {

        MDC.remove(HeaderKey.JOB_ID.getKey());
        MDC.remove(HeaderKey.MSG_ID.getKey());
        MDC.remove(HeaderKey.USER_INFO.getKey());
        MDC.remove(HeaderKey.CLIENT_INFO.getKey());
    }

    protected void createResponseError(@NotNull HttpServletResponse httpResponse, @NotNull BajetErrorResponseDto errorResponseDto) {

        try {

            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            if (errorResponseDto != null) {

                ServletOutputStream outputStream = httpResponse.getOutputStream();
                outputStream.write(this.getObjectMapper().writeValueAsBytes(errorResponseDto));
                outputStream.close();
            }
        } catch (IOException e) {

            appLogger.error("Error On create Response Error", e);
            this.createResponseError(httpResponse, new BajetErrorResponseDto(
                    "",
                    "Error in processing",
                    true));
        }
    }

    @Override
    protected boolean shouldNotFilter(@Nullable HttpServletRequest request) throws ServletException {

        if (request == null)
            return false;

        String requestUri = request.getRequestURI();
        return requestUri.contains("/favicon.ico")
                || requestUri.contains("/error")
                || requestUri.contains("/swagger")
                || requestUri.contains("/swagger-ui")
                || requestUri.contains("/v3/api-docs")
                || requestUri.contains("/h2-console")
                || requestUri.contains("/actuator");
    }
}