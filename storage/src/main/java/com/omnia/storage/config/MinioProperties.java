package com.omnia.storage.config;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".minio")
public class MinioProperties {
    private boolean enabled = false;
    private String endpoint;
    private URI publicHost;
    private int port;
    private String accessKey;
    private String secretKey;
    private String encryptionKey;
    private boolean secure;
    private int preSignedUrlUploadExpirationInMinutes = 10;
    private int preSignedUrlDownloadExpirationInMinutes = 5;
    private String preSignedUrlContentType = "application/octet-stream";
}