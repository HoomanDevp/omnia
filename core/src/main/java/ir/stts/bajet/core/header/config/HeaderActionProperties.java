package ir.stts.bajet.core.header.config;

import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.header.constant.ClientType;
import ir.stts.bajet.core.header.constant.GatewayType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".core.header-action")
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