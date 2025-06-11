package ir.stts.bajet.elastic.config;

import ir.stts.bajet.core.constant.BajetConstants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author pouya rezaei
 */

@Configuration
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".elastic",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true
)
@EnableAutoConfiguration(exclude = {
        ElasticsearchClientAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class,
        ReactiveElasticsearchRepositoriesAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class
})
public class ElasticDisableConfig {
}