package com.omnia.core.otp;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".totp")
@Getter
@Setter
public class TOTPConfig {
    private boolean mockEnabled = false;
}
