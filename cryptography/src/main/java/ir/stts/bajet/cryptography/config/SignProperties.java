package ir.stts.bajet.cryptography.config;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cryptography.sign")
public class SignProperties implements ISignProperties {

    private boolean enabled = false;
    private String curve;
    private String provider;
    private String keyAlgorithm;
    private String algorithm;
    private String keyPairFile;
}