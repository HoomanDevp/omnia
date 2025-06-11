package com.omnia.storage.config;

import io.minio.MinioClient;
import com.omnia.core.constant.BajetConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        MinioProperties.class,
})
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".minio",
        name = "enabled",
        havingValue = "true"
)
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint(), properties.getPort(), properties.isSecure())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}