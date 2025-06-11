package ir.stts.bajet.elastic.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.ilm.PutLifecycleRequest;
import co.elastic.clients.elasticsearch.indices.ExistsIndexTemplateRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutIndexTemplateResponse;
import co.elastic.clients.util.ObjectBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.function.Function;

/**
 * @author pouya rezaei
 */

@Getter
@Setter
@Validated
public abstract class IndexConfig implements InitializingBean {

    private String indexSeparator = "-";
    @NotEmpty(message = "prefix property cant be empty")
    private String prefix;
    @NotEmpty(message = "templatePrefix property cant be empty")
    private String templatePrefix = prefix;
    @NotEmpty(message = "template-name property cant be empty")
    private String templateName;
    @NotNull(message = "number-of-shards property cant be empty")
    private Integer numberOfShards;
    @NotNull(message = "number-of-replicas property cant be empty")
    private Integer numberOfReplicas;
    private Integer maxResultWindow = 10000;


    protected boolean putIndexTemplate(ElasticsearchClient esClient, TypeMapping indexMapping,
                                       Function<IndexSettings.Builder, ObjectBuilder<IndexSettings>> indexSetting,
                                       PutLifecycleRequest policy) throws IOException {

        ExistsIndexTemplateRequest existsIndexTemplateRequest = new ExistsIndexTemplateRequest.Builder()
                .name(templateName)
                .build();

        if (esClient.indices().existsIndexTemplate(existsIndexTemplateRequest).value())
            return true;

        if (policy != null)
            esClient.ilm().putLifecycle(policy);

        PutIndexTemplateResponse putIndexTemplateResponse = esClient.indices()
                .putIndexTemplate((builder -> builder.allowAutoCreate(true).name(templateName)
                        .indexPatterns(templatePrefix + IndexService.WILDCARD)
                        .template(temp -> temp.mappings(indexMapping).settings(indexSetting))));

        return putIndexTemplateResponse.acknowledged();
    }

    protected abstract AbstractIndexService getIndexService();
}