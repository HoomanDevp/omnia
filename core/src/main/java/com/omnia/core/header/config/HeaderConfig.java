package com.omnia.core.header.config;

import com.omnia.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        HeaderActionProperties.class,
        PersianDateSerializerProperties.class
})
public class HeaderConfig {

    @Bean
    public HeaderValidation headerValidation() {
        return new HeaderValidation();
    }
}