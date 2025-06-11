package com.omnia.cryptography.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.key-exchange")
public class KeyExchangeProperties implements IKeyExchangeProperties {

    private boolean enabled = false;
    private String curve;
    private String provider;
    private String algorithm;
    private String keyAlgorithm;
}