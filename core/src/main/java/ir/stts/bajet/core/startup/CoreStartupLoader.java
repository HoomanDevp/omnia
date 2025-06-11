package ir.stts.bajet.core.startup;

import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.message.constant.IMessageCode;
import ir.stts.bajet.core.message.entity.Message;
import ir.stts.bajet.core.message.repository.MessageRepository;
import ir.stts.bajet.core.resilience.constant.IErrorCode;
import ir.stts.bajet.core.resilience.entity.Error;
import ir.stts.bajet.core.resilience.repository.ErrorRepository;
import ir.stts.bajet.core.setting.constant.ISettingCode;
import ir.stts.bajet.core.setting.entity.Setting;
import ir.stts.bajet.core.setting.repository.SettingRepository;
import ir.stts.bajet.core.resilience.exception.ExitException;
import ir.stts.bajet.log.config.LogConfig;
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
import java.util.stream.Collectors;

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

        BajetConstants.ERRORS.addAll(errorRepository.findAll());
        if (!validateConstantsWithDatabase(IErrorCode.class, BajetConstants
                .ERRORS
                .stream()
                .map(Error::getErrorCode)
                .collect(Collectors.toSet())))
            throw new ExitException("Errors not defined in database.");

        BajetConstants.MESSAGES.addAll(messageRepository.findAll());
        if (!validateConstantsWithDatabase(IMessageCode.class, BajetConstants
                .MESSAGES
                .stream()
                .map(Message::getKey)
                .collect(Collectors.toSet())))
            throw new ExitException("Messages not defined in database.");

        BajetConstants.SETTINGS.addAll(settingRepository.findAll());
        if (!validateConstantsWithDatabase(ISettingCode.class, BajetConstants
                .SETTINGS
                .stream()
                .map(Setting::getKey)
                .collect(Collectors.toSet())))
            throw new ExitException("Settings not defined in database.");

        logProperties();
        logSwagger();

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("ir/stts/bajet/sensitive.txt");
            if (inputStream == null) {
                log.warn("Could not find the file in resources: ir/stts/bajet/sensitive.txt");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                List<String> lines = reader.lines().toList();
                LogConfig.SENSITIVE_FIELDS.addAll(lines);
            } catch (IOException e) {
                log.warn("Error reading the sensitive fields file: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Error accessing resources: {}", e.getMessage());
        }

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private <T> boolean validateConstantsWithDatabase(Class<T> markerClass, Set<String> dbKeys) throws IllegalAccessException {

        Set<String> values = new HashSet<>();
        Reflections reflections = new Reflections(BajetConstants.BAJET_BASE_PACKAGE, Scanners.SubTypes);
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