package com.omnia.core.startup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.annotation.ApiMetadata;
import com.omnia.core.constant.BajetConstants;
import com.omnia.core.dto.BajetResponseDto;
import com.omnia.core.dto.gateway.CheckEndpointReq;
import com.omnia.core.dto.gateway.CheckEndpointResp;
import com.omnia.core.dto.gateway.Endpoint;
import com.omnia.core.props.ValidateEndpointConfig;
import com.omnia.core.resilience.exception.ExitException;
import com.omnia.log.config.LogConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(2)
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".core.validate-endpoint",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class EndpointValidator implements CommandLineRunner {

    private static final Pattern VALID_CHARACTERS_PATTERN = Pattern.compile("^(/[a-z0-9\\-{}]*)+$");
    private static final Pattern VERSION_SEGMENT_PATTERN = Pattern.compile(".*/v\\d+/.*");

    private final RequestMappingHandlerMapping handlerMapping;
    private final ObjectMapper objectMapper;
    private final ValidateEndpointConfig config;

    @Override
    public void run(String... args) {
        CheckEndpointReq checkEndpointReq = new CheckEndpointReq()
                .setMaskKeys(LogConfig.SENSITIVE_FIELDS)
                .setAppKey(config.getAppKey());
        List<String> issues = collectEndpoints(checkEndpointReq);

        reportIssues(issues);
        if (!config.isBypassGateway()) {
            validateWithGateway(checkEndpointReq);
        }
    }

    private List<String> collectEndpoints(CheckEndpointReq checkEndpointReq) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        List<String> issues = new ArrayList<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (isExternalController(handlerMethod)) continue;

            Set<String> paths = extractPaths(entry.getKey());
            for (String path : paths) {
                Optional<String> issue = validateEndpoint(path, handlerMethod);
                if (issue.isPresent()) {
                    log.error(issue.get());
                    issues.add(issue.get());
                } else {
                    addValidEndpoint(checkEndpointReq, entry.getKey(), path, handlerMethod);
                }
            }
        }
        return issues;
    }

    private boolean isExternalController(HandlerMethod handlerMethod) {
        String packageName = handlerMethod.getBeanType().getPackage().getName();
        return !packageName.startsWith(BajetConstants.BAJET_BASE_PACKAGE);
    }

    private Set<String> extractPaths(RequestMappingInfo info) {
        return Optional.ofNullable(info.getPathPatternsCondition())
                .map(PathPatternsRequestCondition::getPatternValues)
                .orElse(Collections.emptySet());
    }

    private boolean hasExplicitMappingPath(HandlerMethod method) {
        return Arrays.stream(method.getMethod().getAnnotations())
                .anyMatch(annotation -> {
                    try {
                        Object value = annotation.annotationType().getMethod("value").invoke(annotation);
                        if (value instanceof String[] paths) {
                            return Arrays.stream(paths).anyMatch(StringUtils::hasText);
                        }
                    } catch (Exception ignored) {
                    }
                    return false;
                });
    }

    private Optional<String> validateEndpoint(String path, HandlerMethod method) {
        boolean validChars = VALID_CHARACTERS_PATTERN.matcher(path).matches() && !path.endsWith("/");
        boolean hasVersion = VERSION_SEGMENT_PATTERN.matcher(path).matches();
        boolean hasMetadata = method.getMethod().isAnnotationPresent(ApiMetadata.class);
        boolean hasExplicitPath = hasExplicitMappingPath(method);
        if (validChars && hasVersion && hasMetadata & hasExplicitPath) {
            log.debug("[VALID] ✅ {}", path);
            return Optional.empty();
        }

        StringBuilder issue = new StringBuilder("[INVALID] ❌ ")
                .append(String.format("%s in: %s method: %s", path,
                        method.getBeanType().getSimpleName(), method.getMethod().getName()));
        if (!validChars) issue.append(" [Invalid chars or ends with /]");
        if (!hasVersion) issue.append(" [Missing /v{version}/]");
        if (!hasMetadata) issue.append(" [Missing @ApiMetadata]");
        if (!hasExplicitPath) issue.append(" [Missing explicit path in mapping annotation]");

        return Optional.of(issue.toString());
    }

    private void addValidEndpoint(CheckEndpointReq req, RequestMappingInfo info, String path, HandlerMethod method) {
        ApiMetadata metadata = method.getMethod().getAnnotation(ApiMetadata.class);
        if (metadata == null) return;

        info.getMethodsCondition().getMethods().forEach(requestMethod -> {
            Endpoint endpoint = new Endpoint()
                    .setPath(path)
                    .setMethod(requestMethod.asHttpMethod().name())
                    .setEncrypted(metadata.encrypted())
                    .setAuthenticated(metadata.authenticated())
                    .setBypassGateway(metadata.gatewayBypass());
            req.getEndpoints().add(endpoint);
        });
    }

    private void reportIssues(List<String> issues) {
        if (!issues.isEmpty()) {
            throw new ExitException("❌ Endpoint validation failed with issues.");
        }
    }

    private void validateWithGateway(CheckEndpointReq request) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .messageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                config.getGatewayUrl(), request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ExitException("❌ Failed to validate against Gateway or received null response.");
        }
        try {

            BajetResponseDto<CheckEndpointResp> b = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });

            CheckEndpointResp resp = b.getData();
            if (!resp.getNotAvailableInGateway().isEmpty()
                    || !resp.getNotAvailableInApp().isEmpty()
                    || !resp.getNotAvailableInAuth().isEmpty()) {

                log.error("❌ NotAvailableInGateway: {}", resp.getNotAvailableInGateway());
                log.error("❌ NotAvailableInApp: {}", resp.getNotAvailableInApp());
                log.error("❌ NotAvailableInAuth: {}", resp.getNotAvailableInAuth());
                throw new ExitException("❌ Endpoint mismatch found in gateway validation.");
            }
        } catch (JsonProcessingException e) {
            throw new ExitException("❌ Failed to validate against Gateway json response issue.", e);
        }
    }
}