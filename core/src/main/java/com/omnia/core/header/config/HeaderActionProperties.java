package com.omnia.core.header.config;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.header.constant.ClientType;
import com.omnia.core.header.constant.GatewayType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".core.header-action")
public class HeaderActionProperties {

    private boolean enabled = false;
    private String acceptLanguage;
    private boolean forceClientIP;
    private boolean forceClientVersion;
    private ClientType[] allowedClientType;
    private boolean forceDeviceId;
    private boolean forceAppVersion;
    private GatewayType[] allowedGatewayType;
    private boolean forceUserId;
    private boolean forceClientId;
    private boolean forceUsername;
    private boolean forceNationalCode;
}