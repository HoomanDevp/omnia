package com.omnia.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.omnia.core.util.StringUtil;
import com.omnia.log.LogSpec;
import com.omnia.log.config.LogConfig;
import com.omnia.log.constant.RequestLogAttribute;
import com.omnia.log.constant.ResponseLogAttribute;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;

import static com.omnia.core.constant.OmniaConstants.ORIGINAL_IP_HEADER_KEY;

public abstract class RequestLogSpec extends LogSpec {

    private static final String REQUEST_KEY = "REQUEST";
    private static final String RESPONSE_KEY = "RESPONSE";

    @SneakyThrows
    public static ObjectNode of(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long totalTimeMillis) {

        String ip = request.getHeader(ORIGINAL_IP_HEADER_KEY);
        if (!StringUtils.hasText(ip)) ip = request.getRemoteAddr();

        ObjectNode requestNode = OBJECT_MAPPER.createObjectNode();
        requestNode.put(RequestLogAttribute.IP.getKey(), ip);
        requestNode.put(RequestLogAttribute.METHOD.getKey(), request.getMethod());
        requestNode.put(RequestLogAttribute.URI.getKey(), getFullUrl(request));
        requestNode.set(RequestLogAttribute.BODY.getKey(), getRequestBody(request));
        requestNode.set(RequestLogAttribute.HEADERS.getKey(), getRequestHeaders(request));

        ObjectNode responseNode = OBJECT_MAPPER.createObjectNode();
        responseNode.put(ResponseLogAttribute.STATUS.getKey(), response.getStatus());
        responseNode.put(ResponseLogAttribute.DURATION.getKey(), totalTimeMillis);
        responseNode.set(ResponseLogAttribute.BODY.getKey(), getResponseBody(response));
        responseNode.set(ResponseLogAttribute.HEADERS.getKey(), getResponseHeaders(response));

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.set(REQUEST_KEY, requestNode);
        objectNode.set(RESPONSE_KEY, responseNode);
        maskSensitiveFields(objectNode, LogConfig.REQUEST_DEPTH);
        return objectNode;
    }

    private static JsonNode getRequestHeaders(ContentCachingRequestWrapper request) {

        Enumeration<String> headerNames = request.getHeaderNames();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        while (headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            if (headerName.equals(HttpHeaders.AUTHORIZATION)) {
                objectNode.put(headerName, StringUtil.summarize(request.getHeader(headerName), 50));
            } else objectNode.put(headerName, request.getHeader(headerName));
        }

        return objectNode;
    }

    private static JsonNode getRequestBody(ContentCachingRequestWrapper request) {

        try {
            String contentType = request.getContentType();
            byte[] contentArray = request.getContentAsByteArray();
            if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE) && contentArray.length > 0) {

                String content = new String(contentArray, StandardCharsets.UTF_8);
                return OBJECT_MAPPER.readTree(content);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static JsonNode getResponseHeaders(ContentCachingResponseWrapper response) {

        Collection<String> headerNames = response.getHeaderNames();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        if (!ObjectUtils.isEmpty(headerNames)) for (String headerName : headerNames) {
            if (headerName.equals(HttpHeaders.AUTHORIZATION)) {
                objectNode.put(headerName, StringUtil.summarize(response.getHeader(headerName), 50));
            } else objectNode.put(headerName, response.getHeader(headerName));
        }

        return objectNode;
    }

    private static JsonNode getResponseBody(ContentCachingResponseWrapper response) {

        try {
            String contentType = response.getContentType();
            byte[] contentArray = response.getContentAsByteArray();
            response.copyBodyToResponse();
            if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE) && contentArray.length > 0) {

                String content = new String(contentArray, StandardCharsets.UTF_8);
                return OBJECT_MAPPER.readTree(content);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getFullUrl(ContentCachingRequestWrapper request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        return StringUtils.hasText(queryString) ? requestURL.append('?').append(queryString).toString() : requestURL.toString();
    }
}