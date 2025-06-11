package ir.stts.bajet.core.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    private static final String MESSAGE_FILE_NAME = "messages.properties";

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultLocale(Locale.forLanguageTag("fa-IR"));
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        try {

            Enumeration<URL> resources = getClass().getClassLoader().getResources(MESSAGE_FILE_NAME);
            while (resources.hasMoreElements()) {

                URL resourceUrl = resources.nextElement();
                messageSource.addBasenames(resourceUrl.getFile());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error loading messages.properties dynamically", e);
        }

        messageSource.addBasenames("classpath:messages");

        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean defaultValidator() {

        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());

        return bean;
    }
}