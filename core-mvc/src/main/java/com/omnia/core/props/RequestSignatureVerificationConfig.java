package com.omnia.core.props;

import com.omnia.core.constant.BajetConstants;
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
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".core.request-signature-verification"
)
public class RequestSignatureVerificationConfig {
    private boolean enabled = true;
}
