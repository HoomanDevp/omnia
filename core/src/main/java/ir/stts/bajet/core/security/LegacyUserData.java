package ir.stts.bajet.core.security;

import ir.stts.bajet.core.header.model.HeaderSpec;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class LegacyUserData extends HeaderSpec {

    private String organId;
    private String channel;
    private String phoneNumber;
    private String traceNumber;
    private String referenceNumber;
    private String roles;

    public Map<String, String> toMap() {

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("traceNumber", traceNumber);
        headerMap.put("referenceNumber", referenceNumber);
        headerMap.put("organId", organId);
        headerMap.put("deviceId", getClientInfo().getDeviceId());
        headerMap.put("userId", getUserInfo().getUserId());
        headerMap.put("clientid", getUserInfo().getClientId());
        headerMap.put("nationalcode", getUserInfo().getNationalCode());
        headerMap.put("phonenumber", phoneNumber);
        headerMap.put("roles", roles);
        headerMap.put("username", getUserInfo().getUsername());
        headerMap.put("channel", channel);

        return headerMap;
    }
}