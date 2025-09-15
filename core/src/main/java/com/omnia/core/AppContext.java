package com.omnia.core;

import com.omnia.log.LogSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class AppContext {
    private static final String BEAN_NOT_FOUND_MESSAGE = "Cannot get bean from application context";
    private static ApplicationContext context;

    public AppContext(ApplicationContext context) {
        AppContext.context = context;
    }

    public static Object getBean(String beanName, Object... args) {

        try {
            String targetBeanName = Arrays.stream(context.getBeanDefinitionNames())
                    .filter(name -> name.equalsIgnoreCase(beanName))
                    .findFirst()
                    .orElse(beanName);

            return context.getBean(targetBeanName, args);
        } catch (Exception e) {
            log.error(LogSpec.ofException(BEAN_NOT_FOUND_MESSAGE, e).toString());
        }

        return null;
    }

    public static <T> T getBean(Class<T> clazz, Object... args) {

        try {
            return context.getBean(clazz, args);
        } catch (Exception e) {
            log.error(LogSpec.ofException(BEAN_NOT_FOUND_MESSAGE, e).toString());
        }

        return null;
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {

        try {
            return context.getBean(beanName, clazz);
        } catch (Exception e) {
            log.error(LogSpec.ofException(BEAN_NOT_FOUND_MESSAGE, e).toString());
        }

        return null;
    }


    public static <T> T getProperty(String property, Class<T> clazz, T defaultValue) {

        try {
            return context.getEnvironment().getProperty(property, clazz, defaultValue);
        } catch (Exception e) {
            log.error(LogSpec.ofException(BEAN_NOT_FOUND_MESSAGE, e).toString());
        }

        return defaultValue;
    }
}