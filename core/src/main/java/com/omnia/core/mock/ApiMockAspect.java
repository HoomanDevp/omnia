package com.omnia.core.mock;

import com.omnia.core.AppContext;
import com.omnia.core.constant.BajetConstants;
import com.omnia.core.mock.config.MockProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

@Aspect
@Component
@Profile("!" + BajetConstants.PROD_ENV)
public class ApiMockAspect {

    private Properties mocks;
    private boolean enabled = false;

    public ApiMockAspect(MockProperties properties, Environment environment) {

        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles)
            for (int j = 0; j < properties.getEnvs().length; j++)
                if (properties.getEnvs()[j].equalsIgnoreCase(activeProfile)) {

                    enabled = true;
                    break;
                }

        if (!enabled)
            return;

        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        try {
            yamlFactory.setResources(new ClassPathResource("api-mock.yml"));
        } catch (Exception e) {/*ignore*/}
        this.mocks = Optional.ofNullable(yamlFactory.getObject()).orElseGet(Properties::new);
    }

    @Around("target(com.omnia.core.mock.ApiService)")
    public Object mockApiMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        if (!enabled)
            return joinPoint.proceed();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (className.endsWith("Mock"))
            return joinPoint.proceed();

        String methodName = joinPoint.getSignature().getName();
        String fullKey = className + "." + methodName;

        String mockEnabled = mocks.getProperty(fullKey);
        if ("true".equalsIgnoreCase(mockEnabled)) {

            Object mockBean = AppContext.getBean(joinPoint.getSignature().getDeclaringType().getSimpleName() + "Mock");
            if (mockBean == null)
                throw new IllegalStateException("Mock bean not found for: " + fullKey);

            Method mockMethod = Arrays
                    .stream(mockBean.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == joinPoint.getArgs().length)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Mock method not found for: " + fullKey));

            return mockMethod.invoke(mockBean, joinPoint.getArgs());
        }

        return joinPoint.proceed();
    }
}