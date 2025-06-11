package com.omnia.cryptography.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography")
public class CryptoProperties implements ICryptoProperties {

    private IAesProperties aes;
    private ITripleDesProperties tripleDes;
    private IRsaProperties rsa;
    private ISignProperties sign;
    private IKeyExchangeProperties keyExchange;
}