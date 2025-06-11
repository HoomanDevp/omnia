package com.omnia.elastic.config;

import com.omnia.core.constant.BajetConstants;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".elastic")
public class ElasticProperties {

    private boolean enabled = false;
    @NotEmpty(message = "com.omnia.elastic.nodes property cant be empty")
    private final List<ElasticNode> nodes = new ArrayList<>();
    private String username = "elastic";
    private String password = "admin123";
    private boolean enableQueryLog = false;
    private int timeoutInMillis = 30000;
}