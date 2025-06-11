package ir.stts.bajet.client.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.stts.bajet.log.LogSpec;
import ir.stts.bajet.log.config.LogConfig;
import ir.stts.bajet.log.constant.RequestLogAttribute;
import ir.stts.bajet.log.constant.ResponseLogAttribute;
import lombok.SneakyThrows;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class GatewayLogSpec extends LogSpec {

    private static final String GATEWAY_REQUEST_KEY = "GATEWAY_REQUEST";
    private static final String GATEWAY_RESPONSE_KEY = "GATEWAY_RESPONSE";

    @SneakyThrows
    public static ObjectNode of(Request request, Response response, long totalTimeMillis) {

        ObjectNode requestNode = OBJECT_MAPPER.createObjectNode();
        requestNode.put(RequestLogAttribute.METHOD.getKey(), request.method());
        requestNode.put(RequestLogAttribute.URI.getKey(), request.url().uri().toString());
        requestNode.set(RequestLogAttribute.BODY.getKey(), getRequestBody(request));
        requestNode.set(RequestLogAttribute.HEADERS.getKey(), getRequestHeaders(request));

        ObjectNode responseNode = OBJECT_MAPPER.createObjectNode();
        responseNode.put(RequestLogAttribute.URI.getKey(), request.url().uri().toString());
        responseNode.put(ResponseLogAttribute.STATUS.getKey(), response.code());
        responseNode.put(ResponseLogAttribute.DURATION.getKey(), totalTimeMillis);
        responseNode.set(ResponseLogAttribute.BODY.getKey(), getResponseBody(response));
        responseNode.set(ResponseLogAttribute.HEADERS.getKey(), getResponseHeaders(response));

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.set(GATEWAY_REQUEST_KEY, requestNode);
        objectNode.set(GATEWAY_RESPONSE_KEY, responseNode);
        maskSensitiveFields(objectNode, LogConfig.REQUEST_DEPTH);
        return objectNode;
    }

    @SneakyThrows
    public static ObjectNode of(Request request, Exception e, long totalTimeMillis) {
        ObjectNode requestNode = OBJECT_MAPPER.createObjectNode();
        requestNode.put(RequestLogAttribute.METHOD.getKey(), request.method());
        requestNode.put(RequestLogAttribute.URI.getKey(), request.url().uri().toString());
        requestNode.set(RequestLogAttribute.BODY.getKey(), getRequestBody(request));
        requestNode.set(RequestLogAttribute.HEADERS.getKey(), getRequestHeaders(request));

        ObjectNode responseNode = OBJECT_MAPPER.createObjectNode();
        responseNode.put(RequestLogAttribute.URI.getKey(), request.url().uri().toString());
        responseNode.put(ResponseLogAttribute.STATUS.getKey(), 0);
        responseNode.put(ResponseLogAttribute.DURATION.getKey(), totalTimeMillis);
        responseNode.set(ResponseLogAttribute.BODY.getKey(), LogSpec.ofException(e.getMessage(), e));
        responseNode.set(ResponseLogAttribute.HEADERS.getKey(), NullNode.getInstance());

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.set(GATEWAY_REQUEST_KEY, requestNode);
        objectNode.set(GATEWAY_RESPONSE_KEY, responseNode);
        maskSensitiveFields(objectNode, LogConfig.REQUEST_DEPTH);
        return objectNode;
    }

    private static JsonNode getRequestHeaders(Request request) {

        Headers headers = request.headers();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        for (String name : headers.names())
            objectNode.put(name, headers.get(name));

        return objectNode;
    }

    private static JsonNode getRequestBody(Request request) {

        try {

            if (request.body() != null) {
                Buffer bodyBuffer = new Buffer();
                request.body().writeTo(bodyBuffer);
                return OBJECT_MAPPER.readTree(bodyBuffer.readUtf8());
            }

            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private static JsonNode getResponseHeaders(Response response) {

        Headers headers = response.headers();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        for (String name : headers.names())
            objectNode.put(name, headers.get(name));

        return objectNode;
    }

    @SneakyThrows
    private static JsonNode getResponseBody(Response response) throws IOException {
        if (response.body() != null) {
            byte[] bytes = response.body().source().peek().readByteArray();
            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readTree(jsonString);
        }
        return null;
    }


}