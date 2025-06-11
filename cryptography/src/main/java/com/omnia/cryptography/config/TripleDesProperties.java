package com.omnia.cryptography.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.triple-des")
public class TripleDesProperties implements ITripleDesProperties {

    private boolean enabled = false;
    private String keyAlgorithm;
    private String cipherAlgorithm;
    private int ivSize;
    private int keySize;
    private String defaultIvFile;
    private String defaultKeyFile;
}