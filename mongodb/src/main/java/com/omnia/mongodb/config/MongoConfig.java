package com.omnia.mongodb.config;

import com.omnia.core.constant.BajetConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableConfigurationProperties({
        MongoProperties.class
})
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".mongo",
        name = "enabled",
        havingValue = "true"
)
@EnableMongoRepositories(basePackages = BajetConstants.BAJET_BASE_PACKAGE)
public class MongoConfig {
}