package com.omnia.elastic.config;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.validation.annotation.Validated;

/**
 * @author pouya rezaei
 */

@Getter
@Setter
@Validated
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        ElasticProperties.class
})
@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".elastic",
        name = "enabled",
        havingValue = "true"
)
public class ElasticConfig extends ElasticsearchConfiguration {

    private final ElasticProperties properties;

    @Override
    public ClientConfiguration clientConfiguration() {

        String[] hosts = properties.getNodes()
                .stream()
                .map(ElasticNode::toAddress)
                .toArray(value -> new String[properties.getNodes().size()]);

        return ClientConfiguration.builder()
                .connectedTo(hosts)
                .withClientConfigurer(ElasticsearchClients
                        .ElasticsearchHttpClientConfigurationCallback
                        .from(builder -> {
                            if (properties.isEnableQueryLog())
                                builder.addInterceptorLast(new ElasticQueryLogger());

                            return builder;
                        }))
                .withConnectTimeout(properties.getTimeoutInMillis())
                .withSocketTimeout(properties.getTimeoutInMillis())
                .withBasicAuth(properties.getUsername(), properties.getPassword())
                .build();
    }
}