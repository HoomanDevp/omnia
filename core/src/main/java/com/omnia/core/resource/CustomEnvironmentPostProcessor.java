package com.omnia.core.resource;

import com.omnia.log.LogSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String MODULE_YAML_NAME = "omnia.yml";
    private static final String MODULE_YAML_PATTERN = "classpath*:com//omnia/**/" + MODULE_YAML_NAME;
    private static final String MODULE_RESOURCES_PATTERN = "classpath*:com//omnia/{module}/**/*.yml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        try {

            YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Map<String, ModuleConfig> moduleOrderMap = new HashMap<>();
            Resource[] omniaResources = resolver.getResources(MODULE_YAML_PATTERN);
            for (Resource resource : omniaResources) {

                String moduleName = extractModuleName(resource.getURL().toString());
                ModuleConfig moduleConfig = parseModuleConfig(resource, yamlLoader);
                moduleOrderMap.put(moduleName, moduleConfig);
            }

            List<Map.Entry<String, ModuleConfig>> sortedModules = moduleOrderMap
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparing(entry -> -1 * Optional
                            .ofNullable(entry.getValue().order)
                            .orElse(Integer.MAX_VALUE)))
                    .toList();
            for (Map.Entry<String, ModuleConfig> entry : sortedModules) {

                String moduleName = entry.getKey();
                ModuleConfig moduleConfig = entry.getValue();

                String moduleResourcePattern = MODULE_RESOURCES_PATTERN.replace("{module}", moduleName);
                Resource[] resources = Arrays
                        .stream(resolver.getResources(moduleResourcePattern))
                        .filter(q -> !Objects.equals(q.getFilename(), MODULE_YAML_NAME))
                        .toArray(Resource[]::new);
                if (moduleConfig.ymlOrder != null)
                    Arrays.sort(resources, Comparator.comparing(resource -> {
                        String fileName = resource.getFilename();
                        return -1 * moduleConfig.ymlOrder.getOrDefault(fileName, Integer.MAX_VALUE);
                    }));

                for (Resource resource : resources) {

                    log.info(LogSpec.ofMessage("Loading YAML properties from: " + resource.getURL()).toString());
                    List<PropertySource<?>> propertySources = yamlLoader.load(resource.getFilename(), resource);
                    for (PropertySource<?> propertySource : propertySources)
                        environment.getPropertySources().addLast(
                                new PropertySource<Object>(
                                        moduleName + "-" + propertySource.getName(),
                                        propertySource.getSource()) {
                                    @Override
                                    @Nullable
                                    public Object getProperty(String name) {
                                        return propertySource.getProperty(name);
                                    }
                                });
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load YAML properties", e);
        }
    }

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }

    private String extractModuleName(String url) {

        return url
                .replaceFirst("^.*/(.+)-\\d+\\.\\d+\\.\\d+\\.jar!/.*$", "$1")
                .replaceFirst("^.*/(.+)/target/classes/.*$", "$1");
    }

    private ModuleConfig parseModuleConfig(Resource resource, YamlPropertySourceLoader yamlLoader) throws IOException {

        ModuleConfig config = new ModuleConfig();
        List<PropertySource<?>> propertySources = yamlLoader.load(resource.getFilename(), resource);
        for (PropertySource<?> propertySource : propertySources) {

            Integer order = (Integer) propertySource.getProperty("omnia.module.order");
            if (order != null)
                config.order = order;

            //noinspection unchecked
            config.ymlOrder = ((Map<String, Object>) propertySource.getSource())
                    .entrySet()
                    .stream()
                    .filter(q -> q.getKey().startsWith("omnia.module.yml-order."))
                    .collect(Collectors.toMap(
                            q -> q.getKey().replace("omnia.module.yml-order.", ""),
                            p -> Integer.parseInt(p.getValue().toString())));
        }

        return config;
    }

    private static class ModuleConfig {

        Integer order;
        Map<String, Integer> ymlOrder;

        public ModuleConfig() {
            this.order = null;
            this.ymlOrder = null;
        }
    }
}