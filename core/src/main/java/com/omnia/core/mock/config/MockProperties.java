package com.omnia.core.mock.config;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".core.mock")
public class MockProperties {

    private String[] envs = new String[]{OmniaConstants.DEV_ENV, OmniaConstants.TEST_ENV};
}