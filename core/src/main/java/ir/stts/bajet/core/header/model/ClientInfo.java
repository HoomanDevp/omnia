package ir.stts.bajet.core.header.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import ir.stts.bajet.core.header.constant.ClientType;
import ir.stts.bajet.core.header.constant.GatewayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class ClientInfo {
    
    private String clientIp;
    private String clientVersion;
    private ClientType clientType;
    private String deviceId;
    private String appVersion;
    private GatewayType gatewayType;
}
