package com.omnia.core.props;

import com.omnia.core.constant.OmniaConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".core.validate-endpoint"
)
public class ValidateEndpointConfig {
    private boolean enabled = true;
    private String gatewayUrl;
    private boolean bypassGateway = false;
    private String appKey;
}
