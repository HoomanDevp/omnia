package com.omnia.core.mock.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".core.mock")
public class MockProperties {

    private String[] envs = new String[]{BajetConstants.DEV_ENV, BajetConstants.TEST_ENV};
}