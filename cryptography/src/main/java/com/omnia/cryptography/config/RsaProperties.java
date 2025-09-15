package com.omnia.cryptography.config;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".cryptography.rsa")
public class RsaProperties implements IRsaProperties {

    private boolean enabled = false;
    private String keyAlgorithm;
    private String cipherAlgorithm;
    private int keySize;
    private String defaultKeyPairFile;
}