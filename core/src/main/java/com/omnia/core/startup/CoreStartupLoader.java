package com.omnia.core.startup;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.message.constant.IMessageCode;
import com.omnia.core.message.entity.Message;
import com.omnia.core.message.repository.MessageRepository;
import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.entity.Error;
import com.omnia.core.resilience.exception.ExitException;
import com.omnia.core.resilience.repository.ErrorRepository;
import com.omnia.core.setting.constant.ISettingCode;
import com.omnia.core.setting.entity.Setting;
import com.omnia.core.setting.repository.SettingRepository;
import com.omnia.log.config.LogConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class CoreStartupLoader implements CommandLineRunner {

    private final ErrorRepository errorRepository;
    private final SettingRepository settingRepository;
    private final MessageRepository messageRepository;
    private final ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws IllegalAccessException {

        for (Error error : errorRepository.findAll()) {
            OmniaConstants.ERRORS.put(error.getErrorCode(), error);
        }

        if (!validateConstantsWithDatabase(IErrorCode.class, OmniaConstants.ERRORS.keySet()))
            throw new ExitException("Errors not defined in database.");

        for (Message message : messageRepository.findAll()) {
            OmniaConstants.MESSAGES.put(message.getKey(), message);
        }

        if (!validateConstantsWithDatabase(IMessageCode.class, OmniaConstants.MESSAGES.keySet()))
            throw new ExitException("Messages not defined in database.");

        for (Setting setting : settingRepository.findAll()) {
            OmniaConstants.SETTINGS.put(setting.getKey(), setting);
        }

        if (!validateConstantsWithDatabase(ISettingCode.class, OmniaConstants.SETTINGS.keySet()))
            throw new ExitException("Settings not defined in database.");

        logProperties();
        logSwagger();
        logMemory();

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("com//omnia/sensitive.txt");
            if (inputStream == null) {
                log.warn("Could not find the file in resources: com//omnia/sensitive.txt");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                List<String> lines = reader.lines().toList();
                lines.forEach(line -> {
                    if (line.contains(".")) {
                        LogConfig.SENSITIVE_PATHS.add(line);
                    } else
                        LogConfig.SENSITIVE_FIELDS.add(line);
                });
            } catch (IOException e) {
                log.warn("Error reading the sensitive fields file: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Error accessing resources: {}", e.getMessage());
        }

    }

    private void logMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        log.info("Max memory: {}MB", (maxMemory / 1024 / 1024));
        log.info("Total memory: {}MB", (totalMemory / 1024 / 1024));
        log.info("Free memory: {}MB", (freeMemory / 1024 / 1024));
        log.info("Used memory: {}MB", (usedMemory / 1024 / 1024));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private <T> boolean validateConstantsWithDatabase(Class<T> markerClass, Set<String> dbKeys) throws IllegalAccessException {

        Set<String> values = new HashSet<>();
        Reflections reflections = new Reflections(OmniaConstants.OMNIA_BASE_PACKAGE, Scanners.SubTypes);
        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(markerClass);
        subTypes.add(markerClass);
        for (Class<? extends T> clazz : subTypes)
            for (Field field : clazz.getDeclaredFields()) {

                field.setAccessible(true);
                Object value = field.get(null);
                if (value != null)
                    values.add(value.toString());
            }
        // Find elements that are in constants but not in DB
        Set<String> notFound = new HashSet<>(values);
        notFound.removeAll(dbKeys);

        if (!notFound.isEmpty()) {
            for (String missing : notFound) {
                log.warn("[{}] not found in database.", missing);
            }
        }
        return dbKeys.containsAll(values);
    }

    private void logProperties() {

        ConfigurableEnvironment environment = context.getEnvironment();

        log.debug("=======================================");
        log.debug("         Application Properties        ");
        log.debug("=======================================");

        for (org.springframework.core.env.PropertySource<?> propertySource : environment.getPropertySources())
            if (propertySource.getSource() instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) propertySource.getSource();
                properties.forEach((key, value) -> log.debug("{} = {}", key, value));
            }

        log.debug("=======================================");
    }

    private void logSwagger() {

        ConfigurableEnvironment environment = context.getEnvironment();
        String host = environment.getProperty("JMX_HOST", "localhost");
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.contextPath", "");
        //noinspection HttpUrlsUsage
        StringBuilder base = new StringBuilder("http://")
                .append(host)
                .append(":")
                .append(port)
                .append(contextPath);
        String json = base + environment.getProperty("springdoc.api-docs.path", "/api-docs");
        String ui = base + environment.getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");

        log.info("Swagger JSON: {}", json);
        log.info("Swagger UI: {}", ui);
    }
}