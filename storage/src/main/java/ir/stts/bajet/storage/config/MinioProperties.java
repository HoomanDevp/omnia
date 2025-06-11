package ir.stts.bajet.storage.config;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".minio")
public class MinioProperties {
    private boolean enabled = false;
    private String endpoint;
    private int port;
    private String accessKey;
    private String secretKey;
    private String encryptionKey;
    private boolean secure;
    private int preSignedUrlUploadExpirationInMinutes = 10;
    private int preSignedUrlDownloadExpirationInMinutes = 5;
    private String preSignedUrlContentType = "application/octet-stream";
}