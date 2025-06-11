package ir.stts.bajet.cryptography.config;

import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        CryptoProperties.class,
        AesProperties.class,
        RsaProperties.class,
        SignProperties.class,
        KeyExchangeProperties.class,
        TripleDesProperties.class
})
public class CryptoConfig {

    private final ResourceManager resourceManager;
    private final KeyManagerHelper keyManagerHelper;

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.triple-des",
            name = "enabled",
            havingValue = "true"
    )
    public TripleDesKeyManager tripleDesKeyManager(TripleDesProperties properties) {
        return new TripleDesKeyManager(properties, resourceManager, keyManagerHelper);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.triple-des",
            name = "enabled",
            havingValue = "true"
    )
    public TripleDesManager tripleDesManager(TripleDesProperties properties, TripleDesKeyManager tripleDesKeyManager) {
        return new TripleDesManager(properties, tripleDesKeyManager);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.aes",
            name = "enabled",
            havingValue = "true"
    )
    public AesKeyManager aesKeyManager(AesProperties properties) {
        return new AesKeyManager(properties, resourceManager, keyManagerHelper);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.aes",
            name = "enabled",
            havingValue = "true"
    )
    public AesManager aesManager(AesProperties properties, AesKeyManager aesKeyManager) {
        return new AesManager(properties, aesKeyManager);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.rsa",
            name = "enabled",
            havingValue = "true"
    )
    public RsaKeyManager rsaKeyManager(RsaProperties properties) {
        return new RsaKeyManager(properties, resourceManager, keyManagerHelper);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.rsa",
            name = "enabled",
            havingValue = "true"
    )
    public RsaManager rsaManager(RsaProperties properties, RsaKeyManager rsaKeyManager) {
        return new RsaManager(properties, rsaKeyManager, keyManagerHelper);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.sign",
            name = "enabled",
            havingValue = "true"
    )
    public ECSignKeyManager ecSignKeyManager(SignProperties properties) {
        return new ECSignKeyManager(properties, resourceManager, keyManagerHelper);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.sign",
            name = "enabled",
            havingValue = "true"
    )
    public ECSignManager ecSignManager(SignProperties properties, ECSignKeyManager ecSignKeyManager) {
        return new ECSignManager(properties, ecSignKeyManager);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.key-exchange",
            name = "enabled",
            havingValue = "true"
    )
    public ECKeyExchangeKeyManager ecKeyExchangeKeyManager(KeyExchangeProperties properties) {
        return new ECKeyExchangeKeyManager(properties);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.key-exchange",
            name = "enabled",
            havingValue = "true"
    )
    public ECKeyExchangeManager ecKeyExchangeManager(KeyExchangeProperties properties, ECKeyExchangeKeyManager ecKeyExchangeKeyManager) {
        return new ECKeyExchangeManager(properties, ecKeyExchangeKeyManager);
    }
}