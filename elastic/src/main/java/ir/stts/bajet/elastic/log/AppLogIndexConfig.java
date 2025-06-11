package ir.stts.bajet.elastic.log;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.ilm.PutLifecycleRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.MappingLimitSettings;
import co.elastic.clients.elasticsearch.indices.SegmentSortOrder;
import co.elastic.clients.util.ObjectBuilder;
import ir.stts.bajet.elastic.config.ElasticConfig;
import ir.stts.bajet.elastic.index.AbstractIndexService;
import ir.stts.bajet.elastic.index.IndexConfig;
import ir.stts.bajet.elastic.index.strategy.DailyIndexStrategy;
import ir.stts.bajet.elastic.index.strategy.IndexStrategy;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Setter
@Configuration
@ConditionalOnBean({ElasticConfig.class})
public class AppLogIndexConfig extends IndexConfig {

    public static final String BAJET_LOG_PREFIX = "bajet-logs";
    public static final String BAJET_LOG_INDEX_TEMPLATE_NAME = "bajet-logs-template";
    private static final String TIME_FIELD_TEMPLATE_NAME = "@timestamp";

    private final IndexStrategy indexStrategy;
    private final ElasticsearchClient client;

    private String warmMinAge = "1d";
    private String coldMinAge = "30d";
    private String deleteMinAge = "365d";
    private Long fieldsLimit = 10_000_000L;

    public AppLogIndexConfig(@Value("${spring.application.name}") String applicationName, ElasticsearchClient client) {

        this.indexStrategy = new DailyIndexStrategy();
        this.client = client;
        setPrefix(BAJET_LOG_PREFIX + getIndexSeparator() + applicationName);
        setTemplatePrefix(BAJET_LOG_PREFIX + getIndexSeparator());
        setNumberOfShards(1);
        setNumberOfReplicas(0);
        setTemplateName(BAJET_LOG_INDEX_TEMPLATE_NAME);
    }

    @Override
    @Bean("AppLogIndexService")
    protected AbstractIndexService getIndexService() {
        return new AppLogIndexService(getIndexSeparator(), indexStrategy, getPrefix());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // todo: fix time filed for sorting data
        Time warmMinAgeTime = Time.of(t -> t.time(warmMinAge));
        Time deleteMinAgeTime = Time.of(t -> t.time(deleteMinAge));
        Time coldMinAgeTime = Time.of(t -> t.time(coldMinAge));
        TypeMapping mapping = new TypeMapping.Builder()
                .properties(createMapping())
                .build();

        Function<IndexSettings.Builder, ObjectBuilder<IndexSettings>> indexSettings = cnf -> cnf
                .mapping(MappingLimitSettings.of(builder -> builder.totalFields(n -> n.limit(fieldsLimit))))
                .sort(builder -> builder.field(TIME_FIELD_TEMPLATE_NAME).order(SegmentSortOrder.Desc))
                .lifecycle(index -> index.name(BAJET_LOG_PREFIX + getIndexSeparator() + "ilm"))
                .numberOfShards(getNumberOfShards().toString())
                .numberOfReplicas(getNumberOfReplicas().toString())
                .maxResultWindow(getMaxResultWindow());

        PutLifecycleRequest ilm = PutLifecycleRequest.of(it -> it
                .name(BAJET_LOG_PREFIX + getIndexSeparator() + "ilm")
                .policy(pol -> pol
                        .phases(ph -> ph
                                .warm(w -> w.minAge(warmMinAgeTime)
                                        .actions(builder -> builder))
                                .cold(c -> c.minAge(coldMinAgeTime)
                                        .actions(builder -> builder))
                                .delete(d -> d.minAge(deleteMinAgeTime)
                                        .actions(builder -> builder))
                        )
                )
        );

        boolean isIndexCreate = putIndexTemplate(client, mapping, indexSettings, ilm);
        if (!isIndexCreate)
            throw new RuntimeException("index template creation failed");
    }

    public Map<String, Property> createMapping() {

        return Map.ofEntries(
                Map.entry("@timestamp", Property.of(p -> p
                        .date(d -> d)
                )),
                Map.entry("timestamp", Property.of(p -> p
                        .date(d -> d
                                .format("strict_date_optional_time||epoch_millis||yyyy-MM-dd HH:mm:ss")
                        )
                )),
                Map.entry("level", Property.of(p -> p
                        .keyword(k -> k)
                )),
                Map.entry("logger", Property.of(p -> p
                        .keyword(k -> k)
                )),
                Map.entry("thread", Property.of(p -> p
                        .keyword(k -> k
                                .index(false)
                        )
                )),
                Map.entry("application", Property.of(p -> p
                        .keyword(k -> k
                                .index(false)
                        )
                )),
                Map.entry("job-id", Property.of(p -> p
                        .keyword(k -> k)
                )),
                Map.entry("msg-id", Property.of(p -> p
                        .keyword(k -> k)
                )),
                Map.entry("client-info", Property.of(p -> p
                        .object(o -> o
                                .enabled(true)
                                .subobjects(true)
                        )
                )),
                Map.entry("user-info", Property.of(p -> p
                        .object(o -> o
                                .enabled(true)
                                .subobjects(true)
                        )
                )),
                Map.entry("context", Property.of(p -> p
                        .object(o -> o
                                .enabled(true)
                                .subobjects(true)
                        )
                ))
        );
    }
}